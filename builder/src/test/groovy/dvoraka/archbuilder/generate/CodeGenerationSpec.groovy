package dvoraka.archbuilder.generate

import com.squareup.javapoet.*
import dvoraka.archbuilder.sample.generic.Class2pp
import dvoraka.archbuilder.sample.microservice.data.BaseException
import dvoraka.archbuilder.sample.microservice.data.ResultData
import dvoraka.archbuilder.springconfig.BeanMapping
import dvoraka.archbuilder.springconfig.BeanParameter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import spock.lang.Specification

import javax.lang.model.element.Modifier

class CodeGenerationSpec extends Specification {

    def "Generate parametrized parametrized type"() {
        setup:
            ParameterizedTypeName resultData = ParameterizedTypeName.get(
                    ResultData,
                    BaseException
            )
            ParameterizedTypeName parameterizedTypeName = ParameterizedTypeName.get(
                    ClassName.get(Class2pp),
                    resultData,
                    ClassName.get(BaseException)
            )

            TypeSpec spec = TypeSpec.classBuilder('ParametrizedTypeTest1')
                    .superclass(parameterizedTypeName)
                    .build()
            JavaFile javaFile = JavaFile.builder('', spec)
                    .build()
        expect:
            println javaFile.toString()
    }

    def "Generate Spring configuration"() {
        setup:
            Class<?> type = String
            String name = 'string'
            String body = 'return new String($L)'

            Class<?> parameter1 = String
            String parameterName1 = 'value'

            BeanParameter parameter = new BeanParameter()
            parameter.setType(parameter1)
            parameter.setName(parameterName1)

            BeanMapping mapping = new BeanMapping()
            mapping.setType(type)
            mapping.setName(name)
            mapping.addParameter(parameter)
            mapping.setCode(body)

            MethodSpec methodSpec = MethodSpec.methodBuilder(mapping.getName())
                    .addAnnotation(Bean)
                    .addModifiers(Modifier.PUBLIC)
                    .returns(mapping.getType())
                    .addParameter(parameter1, parameterName1)
                    .addStatement(mapping.getCode(), parameterName1)
                    .build()
            TypeSpec spec = TypeSpec.classBuilder('SpringConfig')
                    .addAnnotation(Configuration)
                    .addMethod(methodSpec)
                    .build()
            JavaFile javaFile = JavaFile.builder('', spec)
                    .build()
        expect:
            println javaFile.toString()
    }
}
