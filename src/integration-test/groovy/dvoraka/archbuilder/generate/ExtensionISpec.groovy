package dvoraka.archbuilder.generate

import dvoraka.archbuilder.DirType
import dvoraka.archbuilder.Directory
import org.springframework.beans.factory.annotation.Autowired

class ExtensionISpec extends BaseISpec {

    @Autowired
    Generator mainGenerator


    def "test"() {
        expect:
            true
    }

    def "simple class extension"() {
        given:
            Directory simpleClass = new Directory.DirectoryBuilder("test")
                    .type(DirType.ABSTRACT)
                    .parent(srcBase)
                    .typeName("dvoraka.archbuilder.test.SimpleClass")
                    .build()
            Directory ext = new Directory.DirectoryBuilder("test")
                    .type(DirType.IMPL)
                    .parent(srcBase)
                    .superType(simpleClass)
                    .filename("BetterSimpleClass")
                    .build()
        when:
            mainGenerator.generate(root)
            Class<?> clazz = loadClass(getClassName(ext))
        then:
            notThrown(Exception)
            isPublicNotAbstract(clazz)
            hasNoTypeParameters(clazz)
            hasNoDeclaredMethods(clazz)
    }
}
