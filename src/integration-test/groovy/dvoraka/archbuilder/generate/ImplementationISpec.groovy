package dvoraka.archbuilder.generate

import dvoraka.archbuilder.DirType
import dvoraka.archbuilder.Directory
import dvoraka.archbuilder.test.InterfaceE1pb
import dvoraka.archbuilder.test.SimpleInterface
import org.springframework.beans.factory.annotation.Autowired

import java.util.concurrent.RunnableFuture

class ImplementationISpec extends BaseISpec {

    @Autowired
    Generator mainGenerator


    def "List implementation"() {
        given:
            Directory abstractList = new Directory.DirectoryBuilder("component")
                    .type(DirType.ABSTRACT)
                    .parent(srcBase)
                    .typeName("java.util.List")
                    .build()
            Directory listImpl = new Directory.DirectoryBuilder("component")
                    .type(DirType.IMPL)
                    .parent(srcBase)
                    .superType(abstractList)
                    .filename("CoolList")
                    .parameterTypeName("java.lang.Integer")
                    .build()
        when:
            mainGenerator.generate(root)
            Class<?> clazz = loadClass(getClassName(listImpl))
        then:
            notThrown(Exception)
            isPublicNotAbstract(clazz)
            hasNoTypeParameters(clazz)
            hasDeclaredMethods(clazz)
            runMethod(clazz, "size") == 0
    }

    def "RunnableFuture abstract implementation"() {
        given:
            Directory abstractRF = new Directory.DirectoryBuilder("component")
                    .type(DirType.ABSTRACT)
                    .parent(srcBase)
                    .typeClass(RunnableFuture.class)
                    .build()
            Directory rfImpl = new Directory.DirectoryBuilder("component")
                    .type(DirType.IMPL)
                    .parent(srcBase)
                    .abstractType()
                    .superType(abstractRF)
                    .filename("AbstractRFImpl")
                    .parameterTypeClass(Integer.class)
                    .build()
        when:
            mainGenerator.generate(root)
            Class<?> clazz = loadClass(getClassName(rfImpl))
        then:
            notThrown(Exception)
            isPublicAbstract(clazz)
            hasNoTypeParameters(clazz)
            hasNoDeclaredMethods(clazz)
    }

    def "simple interface implementation (with type name)"() {
        given:
            Directory simpleInterface = new Directory.DirectoryBuilder("test")
                    .type(DirType.ABSTRACT)
                    .parent(srcBase)
                    .typeName("dvoraka.archbuilder.test.SimpleInterface")
                    .build()
            Directory simpleInterfaceImpl = new Directory.DirectoryBuilder("test")
                    .type(DirType.IMPL)
                    .parent(srcBase)
                    .superType(simpleInterface)
                    .filename("DefaultSimpleInterface")
                    .build()
        when:
            mainGenerator.generate(root)
            Class<?> clazz = loadClass(getClassName(simpleInterfaceImpl))
        then:
            notThrown(Exception)
            isPublicNotAbstract(clazz)
            hasNoTypeParameters(clazz)
            hasNoDeclaredMethods(clazz)
    }

    def "simple interface implementation (with type class)"() {
        given:
            Directory simpleInterface = new Directory.DirectoryBuilder("test")
                    .type(DirType.ABSTRACT)
                    .parent(srcBase)
                    .typeClass(SimpleInterface.class)
                    .build()
            Directory simpleInterfaceImpl = new Directory.DirectoryBuilder("test")
                    .type(DirType.IMPL)
                    .parent(srcBase)
                    .superType(simpleInterface)
                    .filename("DefaultSimpleInterface2")
                    .build()
        when:
            mainGenerator.generate(root)
            Class<?> clazz = loadClass(getClassName(simpleInterfaceImpl))
        then:
            notThrown(Exception)
            isPublicNotAbstract(clazz)
            hasNoTypeParameters(clazz)
            hasNoDeclaredMethods(clazz)
    }

    def "simple interface abstract implementation"() {
        given:
            Directory simpleInterface = new Directory.DirectoryBuilder("test")
                    .type(DirType.ABSTRACT)
                    .parent(srcBase)
                    .typeName("dvoraka.archbuilder.test.SimpleInterface")
                    .build()
            Directory simpleInterfaceImpl = new Directory.DirectoryBuilder("test")
                    .type(DirType.IMPL)
                    .parent(srcBase)
                    .superType(simpleInterface)
                    .abstractType()
                    .filename("AbstractSimpleInterface")
                    .build()
        when:
            mainGenerator.generate(root)
            Class<?> clazz = loadClass(getClassName(simpleInterfaceImpl))
        then:
            notThrown(Exception)
            isPublicAbstract(clazz)
            hasNoTypeParameters(clazz)
            hasNoDeclaredMethods(clazz)
    }

    def "interface E1pb implementation NP"() {
        given:
            Class<?> cls = InterfaceE1pb.class
            Directory abs = new Directory.DirectoryBuilder("test")
                    .type(DirType.ABSTRACT)
                    .parent(srcBase)
                    .typeClass(cls)
                    .build()
            Directory impl = new Directory.DirectoryBuilder("test")
                    .type(DirType.IMPL)
                    .parent(srcBase)
                    .superType(abs)
                    .filename("TestNP" + cls.getSimpleName())
                    .build()
        when:
            mainGenerator.generate(root)
            Class<?> clazz = loadClass(getClassName(impl))
        then:
            notThrown(Exception)
            isPublicNotAbstract(clazz)
            hasTypeParameters(clazz)
            hasDeclaredMethods(clazz)
    }

    def "interface with 4 parameters implementation"() {
        given:
            Directory interface4p = new Directory.DirectoryBuilder("test")
                    .type(DirType.ABSTRACT)
                    .parent(srcBase)
                    .typeName("dvoraka.archbuilder.test.Interface4p1m")
                    .build()
            Directory interface4pImpl = new Directory.DirectoryBuilder("test")
                    .type(DirType.IMPL)
                    .parent(srcBase)
                    .superType(interface4p)
                    .filename("DefaultInterface4P")
                    .parameterTypeName("java.lang.String")
                    .parameterTypeClass(Long.class)
                    .parameterTypeName("java.lang.Boolean")
                    .parameterTypeClass(SimpleInterface.class)
                    .build()
        when:
            mainGenerator.generate(root)
            Class<?> clazz = loadClass(getClassName(interface4pImpl))
        then:
            notThrown(Exception)
            isPublicNotAbstract(clazz)
            hasNoTypeParameters(clazz)
            hasDeclaredMethods(clazz)
    }

    def "interface with 4 parameters abstract implementation"() {
        given:
            Directory interface4p = new Directory.DirectoryBuilder("test")
                    .type(DirType.ABSTRACT)
                    .parent(srcBase)
                    .typeName("dvoraka.archbuilder.test.Interface4p1m")
                    .build()
            Directory interface4pImpl = new Directory.DirectoryBuilder("test")
                    .type(DirType.IMPL)
                    .parent(srcBase)
                    .superType(interface4p)
                    .filename("AbstractInterface4P")
                    .abstractType()
                    .build()
        when:
            mainGenerator.generate(root)
            Class<?> clazz = loadClass(getClassName(interface4pImpl))
        then:
            notThrown(Exception)
            isPublicAbstract(clazz)
            hasTypeParameters(clazz)
            hasNoDeclaredMethods(clazz)
    }
}
