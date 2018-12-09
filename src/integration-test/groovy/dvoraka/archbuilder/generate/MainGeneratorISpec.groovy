package dvoraka.archbuilder.generate

import dvoraka.archbuilder.DirType
import dvoraka.archbuilder.Directory
import dvoraka.archbuilder.service.DirService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired

import static java.nio.file.Files.exists

@Slf4j
class MainGeneratorISpec extends BaseISpec {

    @Autowired
    Generator mainGenerator
    @Autowired
    DirService dirService


    def "abstract Map service"() {
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

    def "abstract List"() {
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
            exists(dirService.getFilePath(srcProps))
    }
}
