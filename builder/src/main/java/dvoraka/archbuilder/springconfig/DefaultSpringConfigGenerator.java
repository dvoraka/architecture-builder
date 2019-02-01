package dvoraka.archbuilder.springconfig;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import dvoraka.archbuilder.exception.GeneratorException;
import dvoraka.archbuilder.generate.JavaHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import javax.lang.model.element.Modifier;
import java.util.List;

@Service
public class DefaultSpringConfigGenerator implements SpringConfigGenerator, JavaHelper {

    @Override
    public String genConfiguration(List<BeanMapping> beanMappings) throws GeneratorException, ClassNotFoundException {

        BeanMapping mapping = beanMappings.get(0);
        Class<?> mappingClass = mapping.getTypeDir() != null
                ? loadClass(mapping.getTypeDir().getTypeName())
                : mapping.getType();

        BeanParameter parameter = mapping.getParameters().get(0);
        Class<?> parameterClass = parameter.getTypeDir() != null
                ? loadClass(parameter.getTypeDir().getTypeName())
                : parameter.getType();

        MethodSpec methodSpec = MethodSpec.methodBuilder(mapping.getName())
                .addAnnotation(Bean.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(mappingClass)
                .addParameter(parameterClass, parameter.getName())
                .addStatement(mapping.getCode(), parameterClass)
                .build();
        TypeSpec spec = TypeSpec.classBuilder("SpringConfig")
                .addAnnotation(Configuration.class)
                .addMethod(methodSpec)
                .build();
        JavaFile javaFile = JavaFile.builder("", spec)
                .build();

        return javaFile.toString();
    }
}
