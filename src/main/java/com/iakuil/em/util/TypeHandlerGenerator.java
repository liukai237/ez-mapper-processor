package com.iakuil.em.util;

import com.iakuil.em.AbstractJsonTypeHandler;
import com.iakuil.em.annotation.JsonEntity;
import javassist.*;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.SignatureAttribute;
import javassist.bytecode.annotation.*;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.io.IOException;

/**
 * TypeHandler生成器
 *
 * <p>如果JavaBean存在{@link JsonEntity}注解，则生成对应的TypeHandler。</p>
 */
public class TypeHandlerGenerator {
    private static final String DEFAULT_CLASS_OUTPUT_DIR = "target/classes";

    private TypeHandlerGenerator() {
    }

    public static void createTypeHandler(String className) {
        ClassPool pool = ClassPool.getDefault();

        // 创建一个XxxTypeHandler空类
        CtClass handlerClazz = pool.makeClass(className + "TypeHandler");

        try {
            // 添加基类
            handlerClazz.setSuperclass(pool.makeClass(AbstractJsonTypeHandler.class.getName()));

            // 添加基类签名
            SignatureAttribute.ClassType sig = new SignatureAttribute.ClassType(
                    AbstractJsonTypeHandler.class.getName(),
                    new SignatureAttribute.TypeArgument[]{
                            new SignatureAttribute.TypeArgument(new SignatureAttribute.ClassType(className))
                    }
            );
            handlerClazz.setGenericSignature(sig.encode());

            // 创建默认构造方法
            CtClass[] params = new CtClass[]{};
            CtConstructor ctor = CtNewConstructor.make(params, null, CtNewConstructor.PASS_PARAMS, null, null, handlerClazz);
            handlerClazz.addConstructor(ctor);
        } catch (CannotCompileException e) {
            throw new IllegalStateException("Occurring an exception during class generating!", e);
        }

        // 处理@MappedTypes和@MappedJdbcTypes注解
        ClassFile ccFile = handlerClazz.getClassFile();
        ConstPool constpool = ccFile.getConstPool();

        Annotation mappedJdbcTypes = new Annotation(MappedJdbcTypes.class.getName(), constpool);
        EnumMemberValue enumMemberValue = new EnumMemberValue(constpool);
        enumMemberValue.setType(JdbcType.class.getName());
        enumMemberValue.setValue("VARCHAR");
        ArrayMemberValue jdbcTypeValues = new ArrayMemberValue(constpool);
        jdbcTypeValues.setValue(new MemberValue[]{enumMemberValue});
        mappedJdbcTypes.addMemberValue("value", jdbcTypeValues);

        Annotation mappedTypes = new Annotation(MappedTypes.class.getName(), constpool);
        ArrayMemberValue mappedTypeValues = new ArrayMemberValue(constpool);
        mappedTypeValues.setValue(new MemberValue[]{new ClassMemberValue(className, constpool)});
        mappedTypes.addMemberValue("value", mappedTypeValues);

        AnnotationsAttribute classAttr = new AnnotationsAttribute(constpool, AnnotationsAttribute.visibleTag);
        classAttr.addAnnotation(mappedTypes);
        classAttr.addAnnotation(mappedJdbcTypes);
        ccFile.addAttribute(classAttr);

        // 生成class文件
        String outputDir = ModuleUtils.getClassOutPutDir(className);
        try {
            handlerClazz.writeFile(outputDir);
        } catch (CannotCompileException | IOException e) {
            throw new IllegalStateException("Occurring an exception during class writing!", e);
        }
    }
}
