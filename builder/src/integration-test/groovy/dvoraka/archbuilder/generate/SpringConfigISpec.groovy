package dvoraka.archbuilder.generate

import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeSpec
import dvoraka.archbuilder.DirType
import dvoraka.archbuilder.Directory
import dvoraka.archbuilder.sample.SimpleClass
import dvoraka.archbuilder.springconfing.BeanMapping
import dvoraka.archbuilder.springconfing.BeanParameter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

import javax.lang.model.element.Modifier
import java.util.function.Supplier


class SpringConfigISpec extends BaseISpec {

    @Autowired
    Generator mainGenerator


    def "Spring config"() {
        setup:
            Directory param1 = new Directory.DirectoryBuilder("test")
                    .type(DirType.ABSTRACT)
                    .parent(srcBase)
                    .typeClass(SimpleClass.class)
                    .build()

            Supplier<String> callback = {
                'test'
            }

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
