package com.dapan.butterknife.compiler;

import com.dapan.butterknife.annotation.ViewBind;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

public class ButterKnifeProcessor extends AbstractProcessor {


    private Elements mElementUtils;
    private Filer filter;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mElementUtils = processingEnv.getElementUtils();
        filter = processingEnv.getFiler();
    }

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
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(ViewBind.class);
        if (elements == null || elements.size() <= 0) {
            System.out.println("-------> not found ViewBind annotation!");
            return false;
        }
        System.out.println("------------>" + elements.size());

        // key: com.dapan.butterknife.MainActivity
        // value: textView1
        Map<Element, List<Element>> elementMap = new LinkedHashMap<>();
        for (Element element : elements) {
            Element activityElement = element.getEnclosingElement();
            System.out.println(activityElement + "-----> " + element.getSimpleName().toString());

            List<Element> viewBindingElements = elementMap.get(activityElement);
            if (viewBindingElements == null) {
                viewBindingElements = new ArrayList<>();
                elementMap.put(activityElement, viewBindingElements);
            }
            viewBindingElements.add(element);
        }

        // 生成代码
        generateJavaFiles(elementMap);

        return false;
    }

    private void generateJavaFiles(Map<Element, List<Element>> elementMap) {
        for (Map.Entry<Element, List<Element>> entry : elementMap.entrySet()) {
            Element key = entry.getKey();
            List<Element> value = entry.getValue();

            // 拿包名
            String pkgName = mElementUtils.getPackageOf(key).getQualifiedName().toString();
            System.out.println("pkg----->" + pkgName);

            generateJavaFile(key, value, pkgName);
        }
    }

    private void generateJavaFile(Element key, List<Element> value, String pkgName) {
        String activityClassNameStr = key.getSimpleName().toString();

        ClassName unBinderInterfaceName = ClassName.get("com.dapan.butterknife", "UnBinder");

        // 构造类
        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(activityClassNameStr + "_ViewBinding")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addSuperinterface(unBinderInterfaceName);

        ClassName activityClassName = ClassName.bestGuess(activityClassNameStr);

        // 添加字段
        classBuilder.addField(FieldSpec.builder(activityClassName, "target").addModifiers(Modifier.PRIVATE).build());

        // 添加构造方法
        classBuilder.addMethod(constructor(value, activityClassName).build());

        // 添加 unbind方法
        classBuilder.addMethod(unbindMethod(value, activityClassName).build()); // 添加方法

        // 生成类
        saveJavaCodeToFile(pkgName, activityClassNameStr, classBuilder);
    }

    /**
     * 构造方法
     *
     * @param value
     * @param activityClassName
     * @return
     */
    private MethodSpec.Builder constructor(List<Element> value, ClassName activityClassName) {
        MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder()
                .addParameter(activityClassName, "target")
                .addModifiers(Modifier.PUBLIC);
        constructorBuilder.addStatement("this.target = target") // 注意，末尾不要加分号
                    .addCode("\n"); // 换行

        // 遍历所有属性，并通过findViewById赋值
        for (Element element : value) {
            String fieldName = element.getSimpleName().toString();
            // com.dapan.butterknife.Utils 在 butterknife 模块下，所以只能通过包名+类名的方式获取
            ClassName utilsClassName = ClassName.get("com.dapan.butterknife", "Utils");
            int viewId = element.getAnnotation(ViewBind.class).value();
            constructorBuilder.addStatement("target.$L = $T.getViewById(target, $L)", fieldName, utilsClassName, viewId);
        }
        return constructorBuilder;
    }

    /**
     * 实现 unbind方法
     * @param value
     * @param activityClassName
     * @return
     */
    private MethodSpec.Builder unbindMethod(List<Element> value, ClassName activityClassName) {
        ClassName callSuperClassName = ClassName.get("androidx.annotation", "CallSuper");
        MethodSpec.Builder unbindMethod = MethodSpec.methodBuilder("unbind")
                .addAnnotation(callSuperClassName)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC);

        unbindMethod.addStatement("$T target = this.target", activityClassName)
                .addCode("\n");
        unbindMethod.beginControlFlow("if (target == null)")
                .addComment("target 为空，直接抛异常")
                .addStatement("throw new IllegalStateException(\"Bindings already cleared.\")")
                .endControlFlow()
                .addCode("\n");
        unbindMethod.addStatement("this.target = null");

        // 解绑
        for (Element element : value) {
            String fieldName = element.getSimpleName().toString();
            unbindMethod.addStatement("target.$L = null", fieldName);
        }
        return unbindMethod;
    }

    /**
     * 保存文件
     * @param pkgName
     * @param activityClassNameStr
     * @param classBuilder
     */
    private void saveJavaCodeToFile(String pkgName, String activityClassNameStr, TypeSpec.Builder classBuilder) {
        try {
            JavaFile.builder(pkgName, classBuilder.build())
                    .addFileComment("手写ButterKnife")
                    .build().writeTo(filter);
            System.out.println(activityClassNameStr + "_ViewBinding.java 已生成！");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("翻车了！！");
        }
    }
}
