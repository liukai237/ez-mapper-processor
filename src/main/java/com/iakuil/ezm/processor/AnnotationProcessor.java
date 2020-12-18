package com.iakuil.ezm.processor;

import com.google.auto.service.AutoService;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtNewConstructor;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.SignatureAttribute;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.ClassMemberValue;
import javassist.bytecode.annotation.EnumMemberValue;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import java.io.File;
import java.util.Set;

@AutoService(Processor.class)
@SupportedAnnotationTypes(value = {"com.iakuil.ezm.processor.annotation.JsonEntity"})
@SupportedSourceVersion(value = SourceVersion.RELEASE_8)
public class AnnotationProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env) {

        File dir = new File("D:\\test\\" + (System.currentTimeMillis() / 1000));
        dir.mkdir();

        for (TypeElement typeElement : annotations) {
            for (Element e : env.getElementsAnnotatedWith(typeElement)) {
                Name typeName = ((TypeElement) e).getQualifiedName();
                try {
                    createTypeHandler(typeName.toString());
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }

        return true;
    }

    private void createTypeHandler(String name) throws Exception {
        Class<?> aClass = Class.forName(name);
        this.createTypeHandler(aClass);
    }

    private void createTypeHandler(Class<?> clazz) throws Exception {
        ClassPool pool = ClassPool.getDefault();

        // 创建一个空类
        CtClass cc = pool.makeClass(clazz.getName() + "TypeHandler");

        // 继承父类及签名
        CtClass parent = pool.makeClass(AbstractJsonTypeHandler.class.getName());
        cc.setSuperclass(parent);
        cc.setGenericSignature(new SignatureAttribute.TypeVariable("AbstractJsonTypeHandler<" + clazz.getSimpleName() + ">").encode());

        // 创建默认构造方法
        CtClass[] params = new CtClass[]{};
        CtConstructor ctor = CtNewConstructor.make(params, null, CtNewConstructor.PASS_PARAMS, null, null, cc);
        cc.addConstructor(ctor);

        // 添加@MappedTypes和@MappedJdbcTypes注解
        ClassFile ccFile = cc.getClassFile();
        ConstPool constpool = ccFile.getConstPool();

        Annotation mappedJdbcTypes = new Annotation(MappedJdbcTypes.class.getName(), constpool);
        EnumMemberValue enumMemberValue = new EnumMemberValue(constpool);
        enumMemberValue.setType(JdbcType.class.getName());
        enumMemberValue.setValue("VARCHAR");
        mappedJdbcTypes.addMemberValue("value", enumMemberValue);

        Annotation mappedTypes = new Annotation(MappedTypes.class.getName(), constpool);
        mappedTypes.addMemberValue("value", new ClassMemberValue(clazz.getName(), constpool));

        AnnotationsAttribute classAttr = new AnnotationsAttribute(constpool, AnnotationsAttribute.visibleTag);
        classAttr.addAnnotation(mappedTypes);
        classAttr.addAnnotation(mappedJdbcTypes);
        ccFile.addAttribute(classAttr);

        // 生成class文件
        cc.writeFile("target/classes");
    }
}
