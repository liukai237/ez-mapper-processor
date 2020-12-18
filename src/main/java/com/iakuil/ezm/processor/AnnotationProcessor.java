package com.iakuil.ezm.processor;

import com.google.auto.service.AutoService;
import com.iakuil.ezm.processor.util.TypeHandlerGenerator;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Set;

/**
 * 注解处理器
 */
@AutoService(Processor.class)
@SupportedAnnotationTypes(value = {"com.iakuil.ezm.processor.annotation.JsonEntity"})
@SupportedSourceVersion(value = SourceVersion.RELEASE_8)
public class AnnotationProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
        Messager messager = processingEnv.getMessager();
        messager.printMessage(Diagnostic.Kind.NOTE, "Start to process the annotations by AnnotationProcessor...");
        for (TypeElement typeElement : annotations) {
            for (Element e : env.getElementsAnnotatedWith(typeElement)) {
                Name typeName = ((TypeElement) e).getQualifiedName();
                messager.printMessage(Diagnostic.Kind.NOTE, "Generating TypeHandler for: " + typeName);
                TypeHandlerGenerator.createTypeHandler(typeName.toString());
            }
        }
        messager.printMessage(Diagnostic.Kind.NOTE, "End to process the annotations by AnnotationProcessor.");
        return true;
    }
}
