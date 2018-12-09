package dvoraka.archbuilder.generate

import dvoraka.archbuilder.DirType
import dvoraka.archbuilder.Directory
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired

@Slf4j
class MainGeneratorISpec extends BaseISpec {

    @Autowired
    Generator mainGenerator


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
