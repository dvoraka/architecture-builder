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
            Directory abs = new Directory.DirectoryBuilder('test')
                    .type(DirType.ABSTRACT)
                    .parent(srcBase)
                    .typeClass(SimpleClass.class)
                    .build()
            Directory ext = new Directory.DirectoryBuilder('config')
                    .type(DirType.IMPL)
                    .parent(srcBase)
                    .superType(abs)
                    .filename('TestSimpleClass')
                    .build()

            Supplier<String> callback = {

                Directory param1 = ext

                Class<?> type = String
                String name = 'string'
                String body = 'return new String($L.toString())'

                Class<?> parameterClass = loadClass(param1.getTypeName())
                String parameterName = 'value'

                BeanParameter parameter = new BeanParameter()
                parameter.setType(parameterClass)
                parameter.setName(parameterName)

                BeanMapping mapping = new BeanMapping()
                mapping.setType(type)
                mapping.setName(name)
                mapping.addParameter(parameter)
                mapping.setCode(body)

                MethodSpec methodSpec = MethodSpec.methodBuilder(mapping.getName())
                        .addAnnotation(Bean)
                        .addModifiers(Modifier.PUBLIC)
                        .returns(mapping.getType())
                        .addParameter(parameter.getType(), parameter.getName())
                        .addStatement(mapping.getCode(), parameterName)
                        .build()
                TypeSpec spec = TypeSpec.classBuilder('SpringConfig')
                        .addAnnotation(Configuration)
                        .addMethod(methodSpec)
                        .build()
                JavaFile javaFile = JavaFile.builder('', spec)
                        .build()

                return javaFile.toString()
            }

            Directory configuration = new Directory.DirectoryBuilder('config')
                    .type(DirType.SPRING_CONFIG)
                    .parent(srcBase)
                    .filename('SpringConfig')
                    .textSupplier(callback)
                    .build()

        expect:
            mainGenerator.generate(root)
    }
}
