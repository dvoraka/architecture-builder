package dvoraka.archbuilder.generate

import dvoraka.archbuilder.DirType
import dvoraka.archbuilder.Directory
import org.springframework.beans.factory.annotation.Autowired

class ExtensionISpec extends BaseISpec {

    @Autowired
    Generator mainGenerator


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

    def "class 1m extension"() {
        given:
            Directory class1m = new Directory.DirectoryBuilder("test")
                    .type(DirType.ABSTRACT)
                    .parent(srcBase)
                    .typeName("dvoraka.archbuilder.test.Class1m")
                    .build()
            Directory ext = new Directory.DirectoryBuilder("test")
                    .type(DirType.IMPL)
                    .parent(srcBase)
                    .superType(class1m)
                    .filename("BetterClass1m")
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

    def "abstract class 1m extension"() {
        given:
            Directory abstractClass1m = new Directory.DirectoryBuilder("test")
                    .type(DirType.ABSTRACT)
                    .parent(srcBase)
                    .typeName("dvoraka.archbuilder.test.AbstractClass1am")
                    .build()
            Directory ext = new Directory.DirectoryBuilder("test")
                    .type(DirType.IMPL)
                    .parent(srcBase)
                    .superType(abstractClass1m)
                    .filename("AbstractClass1mImpl")
                    .build()
        when:
            mainGenerator.generate(root)
            Class<?> clazz = loadClass(getClassName(ext))
        then:
            notThrown(Exception)
            isPublicNotAbstract(clazz)
            hasNoTypeParameters(clazz)
            declaredMethodCount(clazz) == 1
    }

    def "abstract class 1p1am extension"() {
        given:
            Directory abs = new Directory.DirectoryBuilder("test")
                    .type(DirType.ABSTRACT)
                    .parent(srcBase)
                    .typeName("dvoraka.archbuilder.test.AbstractClass1p1am")
                    .build()
            Directory ext = new Directory.DirectoryBuilder("test")
                    .type(DirType.IMPL)
                    .parent(srcBase)
                    .superType(abs)
                    .parameterTypeName("java.lang.String")
                    .filename("AbstractClass1p1amImpl")
                    .build()
        when:
            mainGenerator.generate(root)
            Class<?> clazz = loadClass(getClassName(ext))
        then:
            notThrown(Exception)
            isPublicNotAbstract(clazz)
            hasNoTypeParameters(clazz)
            declaredMethodCount(clazz) == 1
    }

    def "abstract class 1p1am abstract extension"() {
        given:
            Directory abs = new Directory.DirectoryBuilder("test")
                    .type(DirType.ABSTRACT)
                    .parent(srcBase)
                    .typeName("dvoraka.archbuilder.test.AbstractClass1p1am")
                    .build()
            Directory ext = new Directory.DirectoryBuilder("test")
                    .type(DirType.IMPL)
                    .abstractType()
                    .parent(srcBase)
                    .superType(abs)
                    .parameterTypeName("java.lang.String")
                    .filename("AbstractClass1p1amAbs")
                    .build()
        when:
            mainGenerator.generate(root)
            Class<?> clazz = loadClass(getClassName(ext))
        then:
            notThrown(Exception)
            isPublicAbstract(clazz)
            hasNoTypeParameters(clazz)
            hasNoDeclaredMethods(clazz)
    }
}
