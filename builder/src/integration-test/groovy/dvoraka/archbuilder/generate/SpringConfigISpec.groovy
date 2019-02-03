package dvoraka.archbuilder.generate


import dvoraka.archbuilder.DirType
import dvoraka.archbuilder.Directory
import dvoraka.archbuilder.sample.Class1c1m
import dvoraka.archbuilder.sample.SimpleClass
import dvoraka.archbuilder.springconfig.BeanMapping
import dvoraka.archbuilder.springconfig.BeanParameter
import dvoraka.archbuilder.springconfig.SpringConfigGenerator
import dvoraka.archbuilder.springconfig.Templates
import org.springframework.beans.factory.annotation.Autowired

import java.util.function.Supplier

class SpringConfigISpec extends BaseISpec {

    @Autowired
    Generator mainGenerator
    @Autowired
    SpringConfigGenerator springConfigGenerator


    def "Spring config"() {
        given:
            Class<?> cls = SimpleClass
            Directory abs = new Directory.DirectoryBuilder('test')
                    .type(DirType.ABSTRACT)
                    .parent(srcBase)
                    .typeClass(cls)
                    .build()
            Directory ext = new Directory.DirectoryBuilder('config')
                    .type(DirType.IMPL)
                    .parent(srcBase)
                    .superType(abs)
                    .filename('Test' + cls.getSimpleName())
                    .build()

            Class<?> cls2 = Class1c1m
            Directory abs2 = new Directory.DirectoryBuilder('test')
                    .type(DirType.ABSTRACT)
                    .parent(srcBase)
                    .typeClass(cls2)
                    .build()
            Directory ext2 = new Directory.DirectoryBuilder('config')
                    .type(DirType.IMPL)
                    .parent(srcBase)
                    .superType(abs2)
                    .filename('Test' + cls2.getSimpleName())
                    .build()

            // parameters
            BeanParameter strParam = new BeanParameter.Builder('str')
                    .type(String)
                    .build()

            // mappings
            BeanMapping mapping = new BeanMapping.Builder('getBean')
                    .typeDir(abs)
                    .toTypeDir(ext)
                    .codeTemplate({ m -> Templates.simpleReturn(m) })
                    .build()
            BeanMapping mapping2 = new BeanMapping.Builder('getBean')
                    .typeDir(abs2)
                    .toTypeDir(ext2)
                    .addParameter(strParam)
                    .codeTemplate({ m -> Templates.paramReturn(m) })
                    .build()

            List<BeanMapping> beanMappings = new ArrayList<>()
            beanMappings.add(mapping)
            beanMappings.add(mapping2)

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
