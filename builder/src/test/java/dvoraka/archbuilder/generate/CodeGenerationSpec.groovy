package dvoraka.archbuilder.generate

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeSpec
import dvoraka.archbuilder.sample.generic.Class2pp
import dvoraka.archbuilder.sample.microservice.data.BaseException
import dvoraka.archbuilder.sample.microservice.data.ResultData
import spock.lang.Specification

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
            notThrown(Exception)
            println javaFile.toString()
    }

}
