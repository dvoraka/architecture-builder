package dvoraka.archbuilder.generate

import dvoraka.archbuilder.DirType
import dvoraka.archbuilder.Directory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@SpringBootTest
class MainGeneratorISpec extends Specification {

    @Autowired
    Generator mainGenerator

    Directory root
    Directory srcRoot


    def setup() {
        root = new Directory.DirectoryBuilder("rootDir")
                .type(DirType.ROOT)
                .parent(null)
                .build()

        srcRoot = new Directory.DirectoryBuilder("src/main/java")
                .type(DirType.SRC_ROOT)
                .parent(root)
                .build()
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
