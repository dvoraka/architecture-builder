package dvoraka.archbuilder.generate

import dvoraka.archbuilder.DirType
import dvoraka.archbuilder.Directory
import dvoraka.archbuilder.sample.SimpleClass
import dvoraka.archbuilder.sample.SimpleInterface
import dvoraka.archbuilder.sample.SimpleInterface2
import dvoraka.archbuilder.sample.generic.Class1p
import org.springframework.beans.factory.annotation.Autowired

import static dvoraka.archbuilder.generate.Utils.createAbstractDirFor

class ImplAndExtISpec extends BaseISpec {

    @Autowired
    Generator mainGenerator


    def "simple interface implementation and simple class extension"() {
        given:
            Class<?> iface = SimpleInterface
            Class<?> cls = SimpleClass

            Directory simpleInterface = createAbstractDirFor(iface, srcBase)
            Directory simpleClass = createAbstractDirFor(cls, srcBase)
            Directory impl = new Directory.DirectoryBuilder('test')
                    .type(DirType.IMPL)
                    .parent(srcBase)
                    .superType(simpleInterface)
                    .superType(simpleClass)
                    .filename('Test' + cls.getSimpleName() + iface.getSimpleName())
                    .build()
        when:
            mainGenerator.generate(root)
            Class<?> clazz = loadClass(getClassName(impl))
        then:
            notThrown(Exception)
            isPublicNotAbstract(clazz)
            hasNoTypeParameters(clazz)
            hasNoDeclaredMethods(clazz)
            interfaceCount(clazz) == 1
    }

    def "simple interfaces implementation and simple class extension"() {
        given:
            Class<?> iface = SimpleInterface
            Class<?> iface2 = SimpleInterface2
            Class<?> cls = SimpleClass

            Directory simpleInterface = createAbstractDirFor(iface, srcBase)
            Directory simpleInterface2 = createAbstractDirFor(iface2, srcBase)
            Directory simpleClass = createAbstractDirFor(cls, srcBase)
            Directory impl = new Directory.DirectoryBuilder('test')
                    .type(DirType.IMPL)
                    .parent(srcBase)
                    .superType(simpleInterface)
                    .superType(simpleInterface2)
                    .superType(simpleClass)
                    .filename('Test2' + cls.getSimpleName() + iface.getSimpleName())
                    .build()
        when:
            mainGenerator.generate(root)
            Class<?> clazz = loadClass(getClassName(impl))
        then:
            notThrown(Exception)
            isPublicNotAbstract(clazz)
            hasNoTypeParameters(clazz)
            hasNoDeclaredMethods(clazz)
            interfaceCount(clazz) == 2
    }

    def "simple interface implementation and class1p extension NP"() {
        given:
            Class<?> iface = SimpleInterface
            Class<?> cls = Class1p

            Directory simpleInterface = createAbstractDirFor(iface, srcBase)
            Directory clsDir = createAbstractDirFor(cls, srcBase)
            Directory impl = new Directory.DirectoryBuilder('test')
                    .type(DirType.IMPL)
                    .parent(srcBase)
                    .superType(simpleInterface)
                    .superType(clsDir)
                    .filename('TestNP' + cls.getSimpleName() + iface.getSimpleName())
                    .build()
        when:
            mainGenerator.generate(root)
            Class<?> clazz = loadClass(getClassName(impl))
        then:
            notThrown(Exception)
            isPublicNotAbstract(clazz)
            hasTypeParameters(clazz)
            hasNoDeclaredMethods(clazz)
            interfaceCount(clazz) == 1
    }

    def "simple interface implementation and class1p extension"() {
        given:
            Class<?> iface = SimpleInterface
            Class<?> cls = Class1p

            Directory simpleInterface = createAbstractDirFor(iface, srcBase)
            Directory clsDir = createAbstractDirFor(cls, srcBase)
            Directory impl = new Directory.DirectoryBuilder('test')
                    .type(DirType.IMPL)
                    .parent(srcBase)
                    .superType(simpleInterface)
                    .superType(clsDir)
                    .parameterTypeClass(Integer)
                    .filename('Test' + cls.getSimpleName() + iface.getSimpleName())
                    .build()
        when:
            mainGenerator.generate(root)
            Class<?> clazz = loadClass(getClassName(impl))
        then:
            notThrown(Exception)
            isPublicNotAbstract(clazz)
            hasNoTypeParameters(clazz)
            hasNoDeclaredMethods(clazz)
            interfaceCount(clazz) == 1
    }
}
