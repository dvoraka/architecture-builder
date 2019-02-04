package dvoraka.archbuilder.springconfig;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;
import dvoraka.archbuilder.exception.GeneratorException;
import dvoraka.archbuilder.generate.JavaHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import javax.lang.model.element.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DefaultSpringConfigGenerator implements SpringConfigGenerator, JavaHelper {

    @Override
    public String genConfiguration(List<BeanMapping> beanMappings) throws GeneratorException, ClassNotFoundException {

        List<MethodSpec> methodSpecs = new ArrayList<>();
        for (BeanMapping mapping : beanMappings) {

            Class<?> mappingClass = mapping.getTypeDir() != null
                    ? loadClass(mapping.getTypeDir().getTypeName())
                    : mapping.getType();

            Object code = mapping.getCodeTemplate() != null
                    ? mapping.getCodeTemplate().apply(mapping)
                    : mapping.getCode();

            //TODO: add String code
            CodeBlock codeBlock;
            if (code instanceof CodeBlock) {
                codeBlock = (CodeBlock) code;
            } else {
                throw new GeneratorException("Code template must has type CodeBlock.");
            }

            List<ParameterSpec> parameterSpecs = new ArrayList<>();
            for (BeanParameter parameter : mapping.getParameters()) {
                Class<?> parameterClass = parameter.getTypeDir() != null
                        ? loadClass(parameter.getTypeDir().getTypeName())
                        : parameter.getType();

                parameterSpecs.add(ParameterSpec.builder(
                        parameterClass,
                        parameter.getName())
                        .build()
                );
            }

            MethodSpec methodSpec = MethodSpec.methodBuilder(mapping.getName())
                    .addAnnotation(Bean.class)
                    .addModifiers(Modifier.PUBLIC)
                    .returns(mappingClass)
                    .addParameters(parameterSpecs)
                    .addStatement(codeBlock)
                    .build();

            methodSpecs.add(methodSpec);
        }

        TypeSpec spec = TypeSpec.classBuilder("SpringConfig")
                .addAnnotation(Configuration.class)
                .addMethods(methodSpecs)
                .build();
        JavaFile javaFile = JavaFile.builder("", spec)
                .build();

        return javaFile.toString();
    }

    @Override
    public CodeBlock simpleReturn(BeanMapping beanMapping) throws ClassNotFoundException {

        Class<?> returnClass = getReturnClass(beanMapping);

        return CodeBlock.of(
                "return new $T()",
                returnClass
        );
    }

    //TODO
    @Override
    public CodeBlock paramReturn(BeanMapping beanMapping) throws ClassNotFoundException {

        Class<?> returnClass = getReturnClass(beanMapping);

        StringBuilder builder = new StringBuilder("return new $T(");
        for (BeanParameter parameter : beanMapping.getParameters()) {
            builder.append("$L, ");
        }
        builder.append(")");

        List<Object> params = new ArrayList<>();
        params.add(returnClass);
        params.addAll(beanMapping.getParameters().stream()
                .map(BeanParameter::getName)
                .collect(Collectors.toList()));

        params.toArray();

        return CodeBlock.of(
                builder.toString(),
                params
        );
    }

    private Class<?> getReturnClass(BeanMapping beanMapping) throws ClassNotFoundException {
        return beanMapping.getToTypeDir() != null
                ? loadClass(beanMapping.getToTypeDir().getTypeName())
                : beanMapping.getToType();
    }
}
