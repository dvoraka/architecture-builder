package dvoraka.archbuilder.generate

import dvoraka.archbuilder.DirType
import dvoraka.archbuilder.Directory
import org.springframework.beans.factory.annotation.Autowired

class NewTypeISpec extends BaseISpec {

    @Autowired
    Generator mainGenerator


    def "simple new type"() {
        given:
            Directory newType = new Directory.DirectoryBuilder('newtype', DirType.NEW_TYPE)
                    .parent(srcBase)
                    .filename("SimpleNewType")
                    .build()
        when:
            mainGenerator.generate(root)
            Class<?> clazz = loadClass(getClassName(newType))
        then:
            notThrown(Exception)
            isPublicNotAbstract(clazz)
            hasNoTypeParameters(clazz)
            hasNoDeclaredMethods(clazz)
    }

    def "parametrized new type"() {
        given:
            Directory newType = new Directory.DirectoryBuilder('newtype', DirType.NEW_TYPE)
                    .parent(srcBase)
                    .filename("ParametrizedNewType")
                    .parameterTypeName("T")
                    .parameterTypeName("U")
                    .parameterTypeName("V")
                    .build()
        when:
            mainGenerator.generate(root)
            Class<?> clazz = loadClass(getClassName(newType))
        then:
            notThrown(Exception)
            isPublicNotAbstract(clazz)
            hasTypeParameters(clazz)
            hasNoDeclaredMethods(clazz)
    }
}
