package dvoraka.archbuilder.generate

import dvoraka.archbuilder.data.DirType
import dvoraka.archbuilder.data.Directory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import spock.lang.Ignore

class NewTypeISpec extends BaseISpec {

    @Autowired
    Generator mainGenerator


    def "new simple class"() {
        given:
            Directory newType = new Directory.Builder('newtype', DirType.NEW_TYPE)
                    .parent(srcBase)
                    .filename('NewSimpleClass')
                    .build()
        when:
            mainGenerator.generate(root)
            Class<?> clazz = loadClass(newType.getTypeName())
        then:
            notThrown(Exception)
            isPublicNotAbstract(clazz)
            hasNoTypeParameters(clazz)
            hasNoDeclaredMethods(clazz)
    }

    def "new simple class with documentation"() {
        given:
            Directory newType = new Directory.Builder('newtype', DirType.NEW_TYPE)
                    .parent(srcBase)
                    .filename('NewSimpleClass')
                    .doc('Documentation text.\nsecond line\nthird line')
                    .build()
        when:
            mainGenerator.generate(root)
            Class<?> clazz = loadClass(newType.getTypeName())
        then:
            notThrown(Exception)
            isPublicNotAbstract(clazz)
            hasNoTypeParameters(clazz)
            hasNoDeclaredMethods(clazz)
    }

    def "new simple class with annotation"() {
        given:
            Directory newType = new Directory.Builder('newtype', DirType.NEW_TYPE)
                    .parent(srcBase)
                    .filename('NewASimpleClass')
                    .metadata(Service)
                    .build()
        when:
            mainGenerator.generate(root)
            Class<?> clazz = loadClass(newType.getTypeName())
        then:
            notThrown(Exception)
            isPublicNotAbstract(clazz)
            hasNoTypeParameters(clazz)
            hasNoDeclaredMethods(clazz)
            annotationCount(clazz) == 1
    }

    def "new simple class with 2 annotations"() {
        given:
            Directory newType = new Directory.Builder('newtype', DirType.NEW_TYPE)
                    .parent(srcBase)
                    .filename('NewA2SimpleClass')
                    .metadata(Service)
                    .metadata(Deprecated)
                    .build()
        when:
            mainGenerator.generate(root)
            Class<?> clazz = loadClass(newType.getTypeName())
        then:
            notThrown(Exception)
            isPublicNotAbstract(clazz)
            hasNoTypeParameters(clazz)
            hasNoDeclaredMethods(clazz)
            annotationCount(clazz) == 2
    }

    def "new simple interface"() {
        given:
            Directory newType = new Directory.Builder('newtype', DirType.NEW_TYPE)
                    .parent(srcBase)
                    .filename('NewSimpleInterface')
                    .interfaceType()
                    .build()
        when:
            mainGenerator.generate(root)
            Class<?> clazz = loadClass(newType.getTypeName())
        then:
            notThrown(Exception)
            isPublicAbstract(clazz)
            hasNoTypeParameters(clazz)
            hasNoDeclaredMethods(clazz)
            clazz.isInterface()
    }

    def "new parametrized class"() {
        given:
            Directory newType = new Directory.Builder('newtype', DirType.NEW_TYPE)
                    .parent(srcBase)
                    .filename('NewParametrizedType')
                    .parameterType("T")
                    .parameterType("U")
                    .parameterType("V")
                    .build()
        when:
            mainGenerator.generate(root)
            Class<?> clazz = loadClass(newType.getTypeName())
        then:
            notThrown(Exception)
            isPublicNotAbstract(clazz)
            hasTypeParameters(clazz)
            hasNoDeclaredMethods(clazz)
    }

    @Ignore("WIP - missing enum constants")
    def "new simple enum"() {
        given:
            Directory newType = new Directory.Builder('newtype', DirType.NEW_TYPE)
                    .parent(srcBase)
                    .filename('NewSimpleEnum')
                    .enumType()
                    .build()
        when:
            mainGenerator.generate(root)
            Class<?> clazz = loadClass(newType.getTypeName())
        then:
            notThrown(Exception)
            isPublicAbstract(clazz)
            hasNoTypeParameters(clazz)
            hasNoDeclaredMethods(clazz)
            clazz.isEnum()
    }

    def "new simple annotation"() {
        given:
            Directory newType = new Directory.Builder('newtype', DirType.NEW_TYPE)
                    .parent(srcBase)
                    .filename('NewSimpleAnnotation')
                    .annotationType()
                    .build()
        when:
            mainGenerator.generate(root)
            Class<?> clazz = loadClass(newType.getTypeName())
        then:
            notThrown(Exception)
            isPublicAbstract(clazz)
            hasNoTypeParameters(clazz)
            hasNoDeclaredMethods(clazz)
            clazz.isAnnotation()
    }

    def "new simple annotation usage"() {
        given:
            Directory annotation = new Directory.Builder('annotation', DirType.NEW_TYPE)
                    .parent(srcBase)
                    .filename('NewSimpleAnnotation')
                    .annotationType()
                    .build()
            Directory cls = new Directory.Builder('newtype', DirType.NEW_TYPE)
                    .parent(srcBase)
                    .filename('NewAnnotatedClass')
                    .metadata(annotation)
                    .build()
        when:
            mainGenerator.generate(root)
            Class<?> clazz = loadClass(cls.getTypeName())
        then:
            notThrown(Exception)
            isPublicNotAbstract(clazz)
            hasNoTypeParameters(clazz)
            hasNoDeclaredMethods(clazz)
            annotationCount(clazz) == 0 // not runtime policy
    }
}
