package dvoraka.archbuilder.generate

import dvoraka.archbuilder.DirType
import dvoraka.archbuilder.Directory
import dvoraka.archbuilder.sample.generic.*
import org.springframework.beans.factory.annotation.Autowired

class GenericsISpec extends BaseISpec {

    @Autowired
    Generator mainGenerator


    def "abstract class EE2pb3am extension"() {
        given:
            Class<?> cls = AbstractClassEE2pb3am
            Directory abs = new Directory.Builder('test')
                    .type(DirType.ABSTRACT)
                    .parent(srcBase)
                    .typeClass(cls)
                    .build()
            Directory ext = new Directory.Builder('generics')
                    .type(DirType.IMPL)
                    .parent(srcBase)
                    .superType(abs)
                    .filename('Test' + cls.getSimpleName())
                    .parameterTypeClass(Long.class)
                    .parameterTypeClass(Double.class)
                    .build()
        when:
            mainGenerator.generate(root)
            Class<?> clazz = loadClass(getClassName(ext))
        then:
            notThrown(Exception)
            isPublicNotAbstract(clazz)
            hasNoTypeParameters(clazz)
            declaredMethodCount(clazz) == 3
    }

    def "abstract class EE2p4am extension"() {
        given:
            Class<?> cls = AbstractClassEE2p4am
            Directory abs = new Directory.Builder('test')
                    .type(DirType.ABSTRACT)
                    .parent(srcBase)
                    .typeClass(cls)
                    .build()
            Directory ext = new Directory.Builder('generics')
                    .type(DirType.IMPL)
                    .parent(srcBase)
                    .superType(abs)
                    .filename('Test' + cls.getSimpleName())
                    .parameterTypeClass(String.class)
                    .parameterTypeClass(Double.class)
                    .build()
        when:
            mainGenerator.generate(root)
            Class<?> clazz = loadClass(getClassName(ext))
        then:
            notThrown(Exception)
            isPublicNotAbstract(clazz)
            hasNoTypeParameters(clazz)
            declaredMethodCount(clazz) == 4
    }

    def "interface 2p2am implementation"() {
        given:
            Class<?> cls = Interface2p2am
            Directory abs = new Directory.Builder('test')
                    .type(DirType.ABSTRACT)
                    .parent(srcBase)
                    .typeClass(cls)
                    .build()
            Directory impl = new Directory.Builder('generics')
                    .type(DirType.IMPL)
                    .parent(srcBase)
                    .superType(abs)
                    .filename('Test' + cls.getSimpleName())
                    .parameterTypeClass(Long.class)
                    .parameterTypeClass(String.class)
                    .build()
        when:
            mainGenerator.generate(root)
            Class<?> clazz = loadClass(getClassName(impl))
        then:
            notThrown(Exception)
            isPublicNotAbstract(clazz)
            hasNoTypeParameters(clazz)
            declaredMethodCount(clazz) == 2
    }

    def "interface 2p2am implementation NP"() {
        given:
            Class<?> cls = Interface2p2am
            Directory abs = new Directory.Builder('test')
                    .type(DirType.ABSTRACT)
                    .parent(srcBase)
                    .typeClass(cls)
                    .build()
            Directory impl = new Directory.Builder('generics')
                    .type(DirType.IMPL)
                    .parent(srcBase)
                    .superType(abs)
                    .filename('TestNP' + cls.getSimpleName())
                    .build()
        when:
            mainGenerator.generate(root)
            Class<?> clazz = loadClass(getClassName(impl))
        then:
            notThrown(Exception)
            isPublicNotAbstract(clazz)
            hasTypeParameters(clazz)
            declaredMethodCount(clazz) == 2
    }

    def "interface E2p2am implementation"() {
        given:
            Class<?> cls = InterfaceE2p2am
            Directory abs = new Directory.Builder('test')
                    .type(DirType.ABSTRACT)
                    .parent(srcBase)
                    .typeClass(cls)
                    .build()
            Directory impl = new Directory.Builder('generics')
                    .type(DirType.IMPL)
                    .parent(srcBase)
                    .superType(abs)
                    .filename('Test' + cls.getSimpleName())
                    .parameterTypeClass(Long.class)
                    .parameterTypeClass(String.class)
                    .build()
        when:
            mainGenerator.generate(root)
            Class<?> clazz = loadClass(getClassName(impl))
        then:
            notThrown(Exception)
            isPublicNotAbstract(clazz)
            hasNoTypeParameters(clazz)
            declaredMethodCount(clazz) == 2
    }

    def "interface E2p2am implementation NP"() {
        given:
            Class<?> cls = InterfaceE2p2am
            Directory abs = new Directory.Builder('test')
                    .type(DirType.ABSTRACT)
                    .parent(srcBase)
                    .typeClass(cls)
                    .build()
            Directory impl = new Directory.Builder('generics')
                    .type(DirType.IMPL)
                    .parent(srcBase)
                    .superType(abs)
                    .filename('TestNP' + cls.getSimpleName())
                    .build()
        when:
            mainGenerator.generate(root)
            Class<?> clazz = loadClass(getClassName(impl))
        then:
            notThrown(Exception)
            isPublicNotAbstract(clazz)
            hasTypeParameters(clazz)
            declaredMethodCount(clazz) == 2
    }

    def "interface EE2p2am implementation"() {
        given:
            Class<?> cls = InterfaceEE2p2am
            Directory abs = new Directory.Builder('test')
                    .type(DirType.ABSTRACT)
                    .parent(srcBase)
                    .typeClass(cls)
                    .build()
            Directory impl = new Directory.Builder('generics')
                    .type(DirType.IMPL)
                    .parent(srcBase)
                    .superType(abs)
                    .filename('Test' + cls.getSimpleName())
                    .parameterTypeClass(Long.class)
                    .parameterTypeClass(String.class)
                    .build()
        when:
            mainGenerator.generate(root)
            Class<?> clazz = loadClass(getClassName(impl))
        then:
            notThrown(Exception)
            isPublicNotAbstract(clazz)
            hasNoTypeParameters(clazz)
            declaredMethodCount(clazz) == 2
    }

    def "interface EE2p2am implementation NP"() {
        given:
            Class<?> cls = InterfaceEE2p2am
            Directory abs = new Directory.Builder('test')
                    .type(DirType.ABSTRACT)
                    .parent(srcBase)
                    .typeClass(cls)
                    .build()
            Directory impl = new Directory.Builder('generics')
                    .type(DirType.IMPL)
                    .parent(srcBase)
                    .superType(abs)
                    .filename('TestNP' + cls.getSimpleName())
                    .build()
        when:
            mainGenerator.generate(root)
            Class<?> clazz = loadClass(getClassName(impl))
        then:
            notThrown(Exception)
            isPublicNotAbstract(clazz)
            hasTypeParameters(clazz)
            declaredMethodCount(clazz) == 2
    }

    def "interface 2pb2am implementation"() {
        given:
            Class<?> cls = Interface2pb2am
            Directory abs = new Directory.Builder('test')
                    .type(DirType.ABSTRACT)
                    .parent(srcBase)
                    .typeClass(cls)
                    .build()
            Directory impl = new Directory.Builder('generics')
                    .type(DirType.IMPL)
                    .parent(srcBase)
                    .superType(abs)
                    .filename('Test' + cls.getSimpleName())
                    .parameterTypeClass(Long.class)
                    .parameterTypeClass(String.class)
                    .build()
        when:
            mainGenerator.generate(root)
            Class<?> clazz = loadClass(getClassName(impl))
        then:
            notThrown(Exception)
            isPublicNotAbstract(clazz)
            hasNoTypeParameters(clazz)
            declaredMethodCount(clazz) == 2
    }

    def "interface 2pb2am implementation NP"() {
        given:
            Class<?> cls = Interface2pb2am
            Directory abs = new Directory.Builder('test')
                    .type(DirType.ABSTRACT)
                    .parent(srcBase)
                    .typeClass(cls)
                    .build()
            Directory impl = new Directory.Builder('generics')
                    .type(DirType.IMPL)
                    .parent(srcBase)
                    .superType(abs)
                    .filename('TestNP' + cls.getSimpleName())
                    .build()
        when:
            mainGenerator.generate(root)
            Class<?> clazz = loadClass(getClassName(impl))
        then:
            notThrown(Exception)
            isPublicNotAbstract(clazz)
            hasTypeParameters(clazz)
            declaredMethodCount(clazz) == 2
    }
}
