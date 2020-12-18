package com.iakuil.ezm.processor.util;

import com.iakuil.ezm.processor.AbstractJsonTypeHandler;
import javassist.*;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.SignatureAttribute;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.ClassMemberValue;
import javassist.bytecode.annotation.EnumMemberValue;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.io.IOException;

/**
 * TypeHandler生成器
 *
 * <p>如果JavaBean存在{@link com.iakuil.ezm.processor.annotation.JsonEntity}注解，则生成对应的TypeHandler。</p>
 */
public class TypeHandlerGenerator {
    private TypeHandlerGenerator() {
    }

    public static void createTypeHandler(String className) {
        ClassPool pool = ClassPool.getDefault();

        // 创建一个空类
        CtClass cc = pool.makeClass(className + "TypeHandler");

        // 继承父类及签名
        CtClass parent = pool.makeClass(AbstractJsonTypeHandler.class.getName());

        // 创建默认构造方法
        CtClass[] params = new CtClass[]{};

        try {
            cc.setSuperclass(parent);
            cc.setGenericSignature(new SignatureAttribute.TypeVariable("AbstractJsonTypeHandler<" + StringUtils.substringAfterLast(className, ".") + ">").encode());

            CtConstructor ctor = CtNewConstructor.make(params, null, CtNewConstructor.PASS_PARAMS, null, null, cc);
            cc.addConstructor(ctor);
        } catch (CannotCompileException e) {
            throw new IllegalStateException("Occurring an exception during class generating!", e);
        }

        ClassFile ccFile = cc.getClassFile();
        ConstPool constpool = ccFile.getConstPool();

        Annotation mappedJdbcTypes = new Annotation(MappedJdbcTypes.class.getName(), constpool);
        EnumMemberValue enumMemberValue = new EnumMemberValue(constpool);
        enumMemberValue.setType(JdbcType.class.getName());
        enumMemberValue.setValue("VARCHAR");
        mappedJdbcTypes.addMemberValue("value", enumMemberValue);

        Annotation mappedTypes = new Annotation(MappedTypes.class.getName(), constpool);
        mappedTypes.addMemberValue("value", new ClassMemberValue(className, constpool));

        AnnotationsAttribute classAttr = new AnnotationsAttribute(constpool, AnnotationsAttribute.visibleTag);
        classAttr.addAnnotation(mappedTypes);
        classAttr.addAnnotation(mappedJdbcTypes);
        ccFile.addAttribute(classAttr);

        // 生成class文件
        try {
            cc.writeFile("target/classes");
        } catch (CannotCompileException | IOException e) {
            throw new IllegalStateException("Occurring an exception during class writing!", e);
        }
    }
}
