package dvoraka.archbuilder.generate

import com.squareup.javapoet.CodeBlock
import dvoraka.archbuilder.DirType
import dvoraka.archbuilder.Directory
import dvoraka.archbuilder.sample.SimpleClass
import dvoraka.archbuilder.springconfig.BeanMapping
import dvoraka.archbuilder.springconfig.BeanParameter
import dvoraka.archbuilder.springconfig.SpringConfigGenerator
import org.springframework.beans.factory.annotation.Autowired

import java.util.function.Function
import java.util.function.Supplier

class SpringConfigISpec extends BaseISpec {

    @Autowired
    Generator mainGenerator
    @Autowired
    SpringConfigGenerator springConfigGenerator


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

            Function<BeanMapping, CodeBlock> codeTemplate = { mapping ->

                CodeBlock returnCode = CodeBlock.of('return new $T($L)',
                        loadClass(mapping.getToTypeDir().getTypeName()),
                        mapping.getParameters().get(0).getName()
                )

                return returnCode
            }

            Function<BeanMapping, CodeBlock> simpleReturnTemplate = { mapping ->

                CodeBlock returnCode = CodeBlock.of('return new $T()',
                        loadClass(mapping.getToTypeDir().getTypeName())
                )

                return returnCode
            }

            // parameters
            BeanParameter parameter = new BeanParameter.Builder('param1')
                    .typeDir(ext)
                    .build()

            // mappings
            String body = 'return null'
            BeanMapping mapping = new BeanMapping.Builder('getBean')
                    .typeDir(abs)
                    .toTypeDir(ext)
                    .addParameter(parameter)
                    .codeTemplate(simpleReturnTemplate)
                    .build()

            List<BeanMapping> beanMappings = new ArrayList<>()
            beanMappings.add(mapping)

            Supplier<String> callback = {
                return springConfigGenerator.genConfiguration(beanMappings)
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
}
