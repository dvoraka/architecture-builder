package dvoraka.archbuilder.generate

import dvoraka.archbuilder.DirType
import dvoraka.archbuilder.Directory
import dvoraka.archbuilder.sample.Class1m
import dvoraka.archbuilder.sample.SimpleClass
import dvoraka.archbuilder.sample.SimpleInterface
import dvoraka.archbuilder.sample.SimpleInterface2
import dvoraka.archbuilder.sample.generic.Class1p
import dvoraka.archbuilder.sample.generic.Class1p1m
import dvoraka.archbuilder.sample.generic.Interface1p1am
import dvoraka.archbuilder.sample.generic.Interface2p2am
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Ignore

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
            Directory impl = new Directory.Builder('test')
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
            Directory impl = new Directory.Builder('test')
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
            Directory impl = new Directory.Builder('test')
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
            Directory impl = new Directory.Builder('test')
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

//    @Ignore('WIP')
    def "interface1p1am implementation and class1p extension NP"() {
        given:
            Class<?> iface = Interface1p1am
            Class<?> cls = Class1p

            Directory ifaceDir = createAbstractDirFor(iface, srcBase)
            Directory clsDir = createAbstractDirFor(cls, srcBase)
            Directory impl = new Directory.Builder('test')
                    .type(DirType.IMPL)
                    .parent(srcBase)
                    .superType(ifaceDir)
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
            declaredMethodCount(clazz) == 1
            interfaceCount(clazz) == 1
    }

    def "interface1p1am implementation and class1p extension"() {
        given:
            Class<?> iface = Interface1p1am
            Class<?> cls = Class1p

            Directory ifaceDir = createAbstractDirFor(iface, srcBase)
            Directory clsDir = createAbstractDirFor(cls, srcBase)
            Directory impl = new Directory.Builder('test')
                    .type(DirType.IMPL)
                    .parent(srcBase)
                    .superType(ifaceDir)
                    .superType(clsDir)
                    .parameterTypeClass(String)
                    .filename('Test' + cls.getSimpleName() + iface.getSimpleName())
                    .build()
        when:
            mainGenerator.generate(root)
            Class<?> clazz = loadClass(getClassName(impl))
        then:
            notThrown(Exception)
            isPublicNotAbstract(clazz)
            hasNoTypeParameters(clazz)
            declaredMethodCount(clazz) == 1
            interfaceCount(clazz) == 1
    }

    def "interface1p1am implementation and class1m extension"() {
        given:
            Class<?> iface = Interface1p1am
            Class<?> cls = Class1m

            Directory ifaceDir = createAbstractDirFor(iface, srcBase)
            Directory clsDir = createAbstractDirFor(cls, srcBase)
            Directory impl = new Directory.Builder('test')
                    .type(DirType.IMPL)
                    .parent(srcBase)
                    .superType(ifaceDir)
                    .superType(clsDir)
                    .parameterTypeClass(String)
                    .filename('Test' + cls.getSimpleName() + iface.getSimpleName())
                    .build()
        when:
            mainGenerator.generate(root)
            Class<?> clazz = loadClass(getClassName(impl))
        then:
            notThrown(Exception)
            isPublicNotAbstract(clazz)
            hasNoTypeParameters(clazz)
            declaredMethodCount(clazz) == 1
            interfaceCount(clazz) == 1
    }

    def "interface1p1am implementation and class1p1m extension"() {
        given:
            Class<?> iface = Interface1p1am
            Class<?> cls = Class1p1m

            Directory ifaceDir = createAbstractDirFor(iface, srcBase)
            Directory clsDir = createAbstractDirFor(cls, srcBase)
            Directory impl = new Directory.Builder('test')
                    .type(DirType.IMPL)
                    .parent(srcBase)
                    .superType(ifaceDir)
                    .superType(clsDir)
                    .parameterTypeClass(String)
                    .filename('Test' + cls.getSimpleName() + iface.getSimpleName())
                    .build()
        when:
            mainGenerator.generate(root)
            Class<?> clazz = loadClass(getClassName(impl))
        then:
            notThrown(Exception)
            isPublicNotAbstract(clazz)
            hasNoTypeParameters(clazz)
            declaredMethodCount(clazz) == 1
            interfaceCount(clazz) == 1
    }

    @Ignore('WIP - variable parameter count doesn\'t work')
    def "interface1p1am, interface2p2am implementation and class1p1m extension"() {
        given:
            Class<?> iface = Interface1p1am
            Class<?> iface2 = Interface2p2am
            Class<?> cls = Class1p1m

            Directory ifaceDir = createAbstractDirFor(iface, srcBase)
            Directory ifaceDir2 = createAbstractDirFor(iface2, srcBase)
            Directory clsDir = createAbstractDirFor(cls, srcBase)
            Directory impl = new Directory.Builder('test')
                    .type(DirType.IMPL)
                    .parent(srcBase)
                    .superType(ifaceDir)
                    .superType(ifaceDir2)
                    .superType(clsDir)
                    .parameterTypeClass(String)
                    .parameterTypeClass(Long)
                    .filename('Test' + cls.getSimpleName()
                    + iface.getSimpleName() + iface2.getSimpleName())
                    .build()
        when:
            mainGenerator.generate(root)
            Class<?> clazz = loadClass(getClassName(impl))
        then:
            notThrown(Exception)
            isPublicNotAbstract(clazz)
            hasNoTypeParameters(clazz)
            declaredMethodCount(clazz) == 1
            interfaceCount(clazz) == 1
    }
}
