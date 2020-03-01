package com.dapan.butterknife.compiler;

import com.dapan.butterknife.annotation.ViewBind;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

public class ButterKnifeProcessor extends AbstractProcessor {

    @Override
    public SourceVersion getSupportedSourceVersion() {
        System.out.println("1. 指定处理的版本");
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        System.out.println("2. 需要处理的注解");
        Set<String> types = new LinkedHashSet<>();

        types.add(ViewBind.class.getCanonicalName());

        return types;
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        System.out.println("------------>");
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(ViewBind.class);
        for (Element element : elements) {
            System.out.println(element.toString());
        }

        return false;
    }
}
