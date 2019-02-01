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
        given:
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

            // parameters
            BeanParameter parameter = new BeanParameter()
            parameter.setTypeDir(ext)
            parameter.setName("param1")

            // mappings
            String body = 'return new String($L.toString())'
            BeanMapping mapping = new BeanMapping()
            mapping.setType(SimpleClass)
            mapping.setName('getBean')
            mapping.addParameter(parameter)
            mapping.setCode(body)

            List<BeanMapping> beanMappings = new ArrayList<>()
            beanMappings.add(mapping)

            Supplier<String> callback = {
                return genConfiguration(beanMappings)
            }

            Directory configuration = new Directory.DirectoryBuilder('config')
                    .type(DirType.SPRING_CONFIG)
                    .parent(srcBase)
                    .filename('SpringConfig')
                    .textSupplier(callback)
                    .build()
        when:
            mainGenerator.generate(root)
        then:
            notThrown(Exception)
    }

    String genConfiguration(List<BeanMapping> beanMappings) {

        BeanMapping mapping = beanMappings.get(0)
        BeanParameter parameter = mapping.getParameters().get(0)

        Class<?> parameterClass = parameter.getType() != null
                ? parameter.getType()
                : loadClass(parameter.getTypeDir().getTypeName())

        MethodSpec methodSpec = MethodSpec.methodBuilder(mapping.getName())
                .addAnnotation(Bean)
                .addModifiers(Modifier.PUBLIC)
                .returns(mapping.getType())
                .addParameter(parameterClass, parameter.getName())
                .addStatement(mapping.getCode(), parameter.getName())
                .build()
        TypeSpec spec = TypeSpec.classBuilder('SpringConfig')
                .addAnnotation(Configuration)
                .addMethod(methodSpec)
                .build()
        JavaFile javaFile = JavaFile.builder('', spec)
                .build()

        return javaFile.toString()
    }
}
