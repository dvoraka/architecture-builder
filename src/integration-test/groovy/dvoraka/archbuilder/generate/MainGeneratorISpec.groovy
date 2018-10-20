package dvoraka.archbuilder.generate

import dvoraka.archbuilder.DirType
import dvoraka.archbuilder.Directory
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

@Slf4j
@SpringBootTest
class MainGeneratorISpec extends Specification {

    @Autowired
    Generator mainGenerator

    @Shared
    String rootDirName = "testRootDir"

    Directory root
    Directory srcRoot
    Directory srcBase


    def setup() {
        root = new Directory.DirectoryBuilder(rootDirName)
                .type(DirType.ROOT)
                .parent(null)
                .build()

        srcRoot = new Directory.DirectoryBuilder("src/main/java")
                .type(DirType.SRC_ROOT)
                .parent(root)
                .build()

        srcBase = new Directory.DirectoryBuilder("dvoraka/testapp")
                .type(DirType.SRC_BASE)
                .parent(srcRoot)
                .build()
    }

    def cleanup() {
        log.debug("Cleaning up...")

        Path path = Paths.get(rootDirName)
        if (Files.notExists(path)) {
            return
        }

        Files.walk(path)
                .sorted(Comparator.reverseOrder())
                .map({ p -> p.toFile() })
                .peek({ p -> log.debug("Deleting: {}", p) })
                .forEach({ file -> file.delete() })
    }

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
            mainGenerator.generate(abstractMapService)
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
            mainGenerator.generate(abstractList)
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
            mainGenerator.generate(srcProps)
        then:
            notThrown(Exception)
    }
}
