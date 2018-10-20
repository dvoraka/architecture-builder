package dvoraka.archbuilder.generate

import dvoraka.archbuilder.DirType
import dvoraka.archbuilder.Directory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

@SpringBootTest
class MainGeneratorISpec extends Specification {

    @Autowired
    Generator mainGenerator

    @Shared
    String rootDirName = "testRootDir"

    Directory root
    Directory srcRoot


    def setup() {
        root = new Directory.DirectoryBuilder(rootDirName)
                .type(DirType.ROOT)
                .parent(null)
                .build()

        srcRoot = new Directory.DirectoryBuilder("src/main/java")
                .type(DirType.SRC_ROOT)
                .parent(root)
                .build()
    }

    def cleanup() {
        Path path = Paths.get(rootDirName)
        if (Files.notExists(path)) {
            return
        }

        Files.walk(path)
                .sorted(Comparator.reverseOrder())
                .map(Path.&toFile)
                .forEach(System.out.&print)
//                .forEach(File.&delete)
    }

    def "test"() {
        expect:
            true
    }

    def "service abstract"() {
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
}
