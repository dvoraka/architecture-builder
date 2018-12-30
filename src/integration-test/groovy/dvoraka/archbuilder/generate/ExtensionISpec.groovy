package dvoraka.archbuilder.generate

import dvoraka.archbuilder.DirType
import dvoraka.archbuilder.Directory
import dvoraka.archbuilder.test.Class1p2c1m
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

    def "Object abstract extension"() {
        given:
            Directory abstractObject = new Directory.DirectoryBuilder("component")
                    .type(DirType.ABSTRACT)
                    .parent(srcBase)
                    .typeName("java.lang.Object")
                    .build()
            Directory absObjectImpl = new Directory.DirectoryBuilder("component")
                    .type(DirType.IMPL)
                    .parent(srcBase)
                    .superType(abstractObject)
                    .abstractType()
                    .filename("AbstractCoolObject")
                    .build()
        when:
            mainGenerator.generate(root)
            Class<?> clazz = loadClass(getClassName(absObjectImpl))
        then:
            notThrown(Exception)
            isPublicAbstract(clazz)
            hasNoTypeParameters(clazz)
            hasNoDeclaredMethods(clazz)
    }

    def "Object extension"() {
        given:
            Directory abstractObject = new Directory.DirectoryBuilder("component")
                    .type(DirType.ABSTRACT)
                    .parent(srcBase)
                    .typeName("java.lang.Object")
                    .build()
            Directory objectImpl = new Directory.DirectoryBuilder("component")
                    .type(DirType.IMPL)
                    .parent(srcBase)
                    .superType(abstractObject)
                    .filename("CoolObject")
                    .build()
        when:
            mainGenerator.generate(root)
            Class<?> clazz = loadClass(getClassName(objectImpl))
        then:
            notThrown(Exception)
            isPublicNotAbstract(clazz)
            hasNoTypeParameters(clazz)
            hasNoDeclaredMethods(clazz)
    }

    def "Timer extension"() {
        given:
            Directory abstractTimer = new Directory.DirectoryBuilder("component")
                    .type(DirType.ABSTRACT)
                    .parent(srcBase)
                    .typeName("java.util.Timer")
                    .build()
            Directory timerImpl = new Directory.DirectoryBuilder("componentAux")
                    .type(DirType.IMPL)
                    .parent(srcBase)
                    .superType(abstractTimer)
                    .filename("CoolTimer")
                    .build()
        when:
            mainGenerator.generate(root)
            Class<?> clazz = loadClass(getClassName(timerImpl))
        then:
            notThrown(Exception)
            isPublicNotAbstract(clazz)
            hasNoTypeParameters(clazz)
            hasNoDeclaredMethods(clazz)
            clazz.getSuperclass() == Timer.class
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

    def "abstract class 1am extension"() {
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
                    .filename("TestAbstractClass1am")
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

    def "class 1p2c1m extension"() {
        given:
            Directory abs = new Directory.DirectoryBuilder("test")
                    .type(DirType.ABSTRACT)
                    .parent(srcBase)
                    .typeClass(Class1p2c1m.class)
                    .build()
            Directory ext = new Directory.DirectoryBuilder("test")
                    .type(DirType.IMPL)
                    .parent(srcBase)
                    .superType(abs)
                    .parameterTypeClass(Long.class)
                    .filename("TestClass1p2c1m")
                    .build()
        when:
            mainGenerator.generate(root)
            Class<?> clazz = loadClass(getClassName(ext))
        then:
            notThrown(Exception)
            isPublic(clazz)
            hasNoTypeParameters(clazz)
            hasNoDeclaredMethods(clazz)
    }
}
