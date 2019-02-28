package dvoraka.archbuilder.generate

import dvoraka.archbuilder.DirType
import dvoraka.archbuilder.Directory
import dvoraka.archbuilder.sample.SimpleInterface
import dvoraka.archbuilder.sample.generic.Interface4p1m
import dvoraka.archbuilder.sample.generic.InterfaceE1pb
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import java.util.concurrent.RunnableFuture

class ImplementationISpec extends BaseISpec {

    @Autowired
    Generator mainGenerator


    def "List implementation"() {
        given:
            Class<?> cls = List
            Directory abs = new Directory.DirectoryBuilder('component', DirType.ABSTRACT)
                    .parent(srcBase)
                    .typeClass(cls)
                    .build()
            Directory impl = new Directory.DirectoryBuilder('component', DirType.IMPL)
                    .parent(srcBase)
                    .superType(abs)
                    .filename('Test' + cls.getSimpleName())
                    .parameterTypeName('java.lang.Integer')
                    .build()
        when:
            mainGenerator.generate(root)
            Class<?> clazz = loadClass(getClassName(impl))
        then:
            notThrown(Exception)
            isPublicNotAbstract(clazz)
            hasNoTypeParameters(clazz)
            hasDeclaredMethods(clazz)
            runMethod(clazz, 'size') == 0
    }

    def "List implementation with annotation"() {
        given:
            Class<?> cls = List
            Directory abs = new Directory.DirectoryBuilder('component', DirType.ABSTRACT)
                    .parent(srcBase)
                    .typeClass(cls)
                    .build()
            Directory impl = new Directory.DirectoryBuilder('component', DirType.IMPL)
                    .parent(srcBase)
                    .superType(abs)
                    .filename('TestA' + cls.getSimpleName())
                    .metadataClass(Service)
                    .parameterTypeName('java.lang.Integer')
                    .build()
        when:
            mainGenerator.generate(root)
            Class<?> clazz = loadClass(getClassName(impl))
        then:
            notThrown(Exception)
            isPublicNotAbstract(clazz)
            hasNoTypeParameters(clazz)
            hasDeclaredMethods(clazz)
            runMethod(clazz, 'size') == 0
            annotationCount(clazz) == 1
    }

    def "RunnableFuture abstract implementation"() {
        given:
            Directory abs = new Directory.DirectoryBuilder("component", DirType.ABSTRACT)
                    .parent(srcBase)
                    .typeClass(RunnableFuture.class)
                    .build()
            Directory impl = new Directory.DirectoryBuilder("component", DirType.IMPL)
                    .parent(srcBase)
                    .abstractType()
                    .superType(abs)
                    .filename("AbstractRFImpl")
                    .parameterTypeClass(Integer.class)
                    .build()
        when:
            mainGenerator.generate(root)
            Class<?> clazz = loadClass(getClassName(impl))
        then:
            notThrown(Exception)
            isPublicAbstract(clazz)
            hasNoTypeParameters(clazz)
            hasNoDeclaredMethods(clazz)
    }

    def "simple interface implementation (with type name)"() {
        given:
            Directory abs = new Directory.DirectoryBuilder('test', DirType.ABSTRACT)
                    .parent(srcBase)
                    .typeName("dvoraka.archbuilder.sample.SimpleInterface")
                    .build()
            Directory impl = new Directory.DirectoryBuilder('test', DirType.IMPL)
                    .parent(srcBase)
                    .superType(abs)
                    .filename("DefaultSimpleInterface")
                    .build()
        when:
            mainGenerator.generate(root)
            Class<?> clazz = loadClass(getClassName(impl))
        then:
            notThrown(Exception)
            isPublicNotAbstract(clazz)
            hasNoTypeParameters(clazz)
            hasNoDeclaredMethods(clazz)
    }

    def "simple interface implementation (with type class)"() {
        given:
            Directory abs = new Directory.DirectoryBuilder('test', DirType.ABSTRACT)
                    .parent(srcBase)
                    .typeClass(SimpleInterface.class)
                    .build()
            Directory impl = new Directory.DirectoryBuilder('test', DirType.IMPL)
                    .parent(srcBase)
                    .superType(abs)
                    .filename("DefaultSimpleInterface2")
                    .build()
        when:
            mainGenerator.generate(root)
            Class<?> clazz = loadClass(getClassName(impl))
        then:
            notThrown(Exception)
            isPublicNotAbstract(clazz)
            hasNoTypeParameters(clazz)
            hasNoDeclaredMethods(clazz)
    }

    def "simple interface abstract implementation (with type name)"() {
        given:
            Directory abs = new Directory.DirectoryBuilder('test', DirType.ABSTRACT)
                    .parent(srcBase)
                    .typeName("dvoraka.archbuilder.sample.SimpleInterface")
                    .build()
            Directory impl = new Directory.DirectoryBuilder('test', DirType.IMPL)
                    .parent(srcBase)
                    .superType(abs)
                    .abstractType()
                    .filename("AbstractSimpleInterface")
                    .build()
        when:
            mainGenerator.generate(root)
            Class<?> clazz = loadClass(getClassName(impl))
        then:
            notThrown(Exception)
            isPublicAbstract(clazz)
            hasNoTypeParameters(clazz)
            hasNoDeclaredMethods(clazz)
    }

    def "simple interface abstract implementation implementation"() {
        given:
            Class<?> cls = SimpleInterface.class
            Directory abs = new Directory.DirectoryBuilder('test', DirType.ABSTRACT)
                    .parent(srcBase)
                    .typeClass(cls)
                    .build()
            Directory impl1 = new Directory.DirectoryBuilder('test', DirType.IMPL)
                    .parent(srcBase)
                    .superType(abs)
                    .abstractType()
                    .filename('TestAbstract1' + cls.getSimpleName())
                    .build()
            Directory impl2 = new Directory.DirectoryBuilder('test', DirType.IMPL)
                    .parent(srcBase)
                    .superType(impl1)
                    .filename('Test2' + cls.getSimpleName())
                    .build()
        when:
            mainGenerator.generate(root)
            Class<?> clazz = loadClass(getClassName(impl2))
        then:
            notThrown(Exception)
            isPublicNotAbstract(clazz)
            hasNoTypeParameters(clazz)
            hasNoDeclaredMethods(clazz)
    }

    def "interface E1pb implementation NP"() {
        given:
            Class<?> cls = InterfaceE1pb
            Directory abs = new Directory.DirectoryBuilder('test', DirType.ABSTRACT)
                    .parent(srcBase)
                    .typeClass(cls)
                    .build()
            Directory impl = new Directory.DirectoryBuilder('test', DirType.IMPL)
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
            Class<?> cls = Interface4p1m.class
            Directory abs = new Directory.DirectoryBuilder('test', DirType.ABSTRACT)
                    .parent(srcBase)
                    .typeClass(cls)
                    .build()
            Directory impl = new Directory.DirectoryBuilder('test', DirType.IMPL)
                    .parent(srcBase)
                    .superType(abs)
                    .filename('Test' + cls.getSimpleName())
                    .parameterTypeName("java.lang.String")
                    .parameterTypeClass(Long.class)
                    .parameterTypeName("java.lang.Boolean")
                    .parameterTypeClass(SimpleInterface.class)
                    .build()
        when:
            mainGenerator.generate(root)
            Class<?> clazz = loadClass(getClassName(impl))
        then:
            notThrown(Exception)
            isPublicNotAbstract(clazz)
            hasNoTypeParameters(clazz)
            hasDeclaredMethods(clazz)
    }

    def "interface with 4 parameters implementation extension"() {
        given:
            Class<?> cls = Interface4p1m.class
            Directory abs = new Directory.DirectoryBuilder('test', DirType.ABSTRACT)
                    .parent(srcBase)
                    .typeClass(cls)
                    .build()
            Directory impl1 = new Directory.DirectoryBuilder('test', DirType.IMPL)
                    .parent(srcBase)
                    .superType(abs)
                    .filename('Test1' + cls.getSimpleName())
                    .parameterTypeName("java.lang.String")
                    .parameterTypeClass(Long.class)
                    .parameterTypeName("java.lang.Boolean")
                    .parameterTypeClass(SimpleInterface.class)
                    .build()
            Directory impl2 = new Directory.DirectoryBuilder('test2', DirType.IMPL)
                    .parent(srcBase)
                    .superType(impl1)
                    .filename('Test2' + cls.getSimpleName())
                    .build()
        when:
            mainGenerator.generate(root)
            Class<?> clazz = loadClass(getClassName(impl2))
        then:
            notThrown(Exception)
            isPublicNotAbstract(clazz)
            hasNoTypeParameters(clazz)
            hasNoDeclaredMethods(clazz)
    }

    def "interface with 4 parameters abstract implementation"() {
        given:
            Class<?> cls = Interface4p1m.class
            Directory interface4p = new Directory.DirectoryBuilder('test', DirType.ABSTRACT)
                    .parent(srcBase)
                    .typeClass(cls)
                    .build()
            Directory interface4pImpl = new Directory.DirectoryBuilder('impl', DirType.IMPL)
                    .parent(srcBase)
                    .superType(interface4p)
                    .filename('Test' + cls.getSimpleName())
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
