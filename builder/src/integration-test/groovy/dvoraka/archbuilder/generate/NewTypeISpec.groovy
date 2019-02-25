package dvoraka.archbuilder.generate

import dvoraka.archbuilder.DirType
import dvoraka.archbuilder.Directory
import org.springframework.beans.factory.annotation.Autowired

class NewTypeISpec extends BaseISpec {

    @Autowired
    Generator mainGenerator


    def "new simple class"() {
        given:
            Directory newType = new Directory.DirectoryBuilder('newtype', DirType.NEW_TYPE)
                    .parent(srcBase)
                    .filename('NewSimpleClass')
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

    def "new simple interface"() {
        given:
            Directory newType = new Directory.DirectoryBuilder('newtype', DirType.NEW_TYPE)
                    .parent(srcBase)
                    .filename('NewSimpleInterface')
                    .interfaceType()
                    .build()
        when:
            mainGenerator.generate(root)
            Class<?> clazz = loadClass(getClassName(newType))
        then:
            notThrown(Exception)
            isPublicAbstract(clazz)
            hasNoTypeParameters(clazz)
            hasNoDeclaredMethods(clazz)
    }

    def "new parametrized class"() {
        given:
            Directory newType = new Directory.DirectoryBuilder('newtype', DirType.NEW_TYPE)
                    .parent(srcBase)
                    .filename('NewParametrizedType')
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
