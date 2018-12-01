package dvoraka.archbuilder.generate

import dvoraka.archbuilder.DirType
import dvoraka.archbuilder.Directory
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired

@Slf4j
class MainGeneratorISpec extends BaseISpec {

    //TODO: split to implementation, extension, ...

    @Autowired
    Generator mainGenerator


    def "test"() {
        expect:
            true
    }

    def "abstract map service"() {
        given:
            Directory abstractMapService = new Directory.DirectoryBuilder("service")
                    .type(DirType.SERVICE_ABSTRACT)
                    .parent(root)
                    .typeName("java.util.Map")
                    .build()
        when:
            mainGenerator.generate(root)
        then:
            notThrown(Exception)
    }

    def "abstract list"() {
        given:
            Directory abstractList = new Directory.DirectoryBuilder("component")
                    .type(DirType.ABSTRACT)
                    .parent(srcBase)
                    .typeName("java.util.List")
                    .build()
        when:
            mainGenerator.generate(root)
        then:
            notThrown(Exception)
    }

    def "src properties"() {
        given:
            Directory srcProps = new Directory.DirectoryBuilder("src/main/resources")
                    .type(DirType.SRC_PROPERTIES)
                    .parent(root)
                    .filename("application.properties")
                    .text("prop1=value\nprop2=value2\n")
                    .build()
        when:
            mainGenerator.generate(root)
        then:
            notThrown(Exception)
    }

    def "map service implementation"() {
        given:
            Directory abstractMapService = new Directory.DirectoryBuilder("service")
                    .type(DirType.SERVICE_ABSTRACT)
                    .parent(srcBaseAbs)
                    .typeName("java.util.Map")
                    .build()
            Directory mapService = new Directory.DirectoryBuilder("service")
                    .type(DirType.SERVICE)
                    .parent(srcBase)
                    .superType(abstractMapService)
                    .filename("CoolMapService")
                    .parameterType("java.lang.String")
                    .parameterType("java.lang.Long")
                    .build()
            Directory mapService1Impl = new Directory.DirectoryBuilder("service")
                    .type(DirType.SERVICE_IMPL)
                    .parent(srcBase)
                    .superType(mapService)
                    .build()
        when:
            mainGenerator.generate(root)
            Class<?> clazz = loadClass(
                    mapService1Impl.getPackageName()
                            + "."
                            + "Default" + mapService.getFilename()
                            .orElseThrow({ new RuntimeException() })
            )
        then:
            notThrown(Exception)
            isPublicNotAbstract(clazz)
            hasNoTypeParameters(clazz)
            hasDeclaredMethods(clazz)
    }

    def "runnable future service implementation"() {
        given:
            Directory abstractRFService = new Directory.DirectoryBuilder("service")
                    .type(DirType.SERVICE_ABSTRACT)
                    .parent(srcBaseAbs)
                    .typeName("java.util.concurrent.RunnableFuture")
                    .build()
            Directory rfService = new Directory.DirectoryBuilder("service")
                    .type(DirType.SERVICE)
                    .parent(srcBase)
                    .superType(abstractRFService)
                    .filename("RFService")
                    .parameterType("java.lang.String")
                    .build()
            Directory rfService1Impl = new Directory.DirectoryBuilder("service2")
                    .type(DirType.SERVICE_IMPL)
                    .parent(srcBase)
                    .superType(rfService)
                    .build()
        when:
            mainGenerator.generate(root)
        then:
            notThrown(Exception)
    }

    def "list implementation"() {
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
                    .parameterType("java.lang.Integer")
                    .build()
        when:
            mainGenerator.generate(root)
            Class<?> clazz = loadClass(getClassName(listImpl))
        then:
            notThrown(Exception)
            isPublicNotAbstract(clazz)
            hasNoTypeParameters(clazz)
    }

    def "abstract object implementation"() {
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
    }

    def "object implementation"() {
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
    }

    def "simple interface implementation"() {
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
                    .parameterType("java.lang.String")
                    .parameterType("java.lang.Long")
                    .parameterType("java.lang.Boolean")
                    .parameterType("dvoraka.archbuilder.test.SimpleInterface")
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

    def "class 1M extension"() {
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
                    .filename("BetterClass1M")
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

    def "abstract class 1M extension"() {
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
                    .filename("AbstractClass1MImpl")
                    .build()
        when:
            mainGenerator.generate(root)
            Class<?> clazz = loadClass(getClassName(ext))
        then:
            notThrown(Exception)
            isPublicNotAbstract(clazz)
            hasNoTypeParameters(clazz)
            hasDeclaredMethods(clazz)
    }

    def "abstract class 1P1M extension"() {
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
                    .parameterType("java.lang.String")
                    .filename("AbstractClass1P1MImpl")
                    .build()
        when:
            mainGenerator.generate(root)
            Class<?> clazz = loadClass(getClassName(ext))
        then:
            notThrown(Exception)
            isPublicNotAbstract(clazz)
            hasNoTypeParameters(clazz)
            hasDeclaredMethods(clazz)
    }

    def "timer implementation"() {
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
            clazz.getSuperclass() == Timer.class
    }
}
