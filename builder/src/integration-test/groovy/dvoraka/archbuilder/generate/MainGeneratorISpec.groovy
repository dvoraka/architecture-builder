package dvoraka.archbuilder.generate

import dvoraka.archbuilder.data.DirType
import dvoraka.archbuilder.data.Directory
import dvoraka.archbuilder.service.DirService
import dvoraka.archbuilder.util.TextBuilder
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
            Directory abstractMapService = new Directory.Builder('service', DirType.SERVICE_ABSTRACT)
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
            Directory abstractList = new Directory.Builder('component', DirType.ABSTRACT)
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
            String text = new TextBuilder()
                    .addLn('prop1=value')
                    .addLn('prop2=value2')
                    .getText()
            Directory srcProps = new Directory.Builder('src/main/resources', DirType.TEXT)
                    .parent(root)
                    .filename('application.properties')
                    .text(text)
                    .build()
        when:
            mainGenerator.generate(root)
        then:
            notThrown(Exception)
            exists(dirService.getFilePath(srcProps))
    }

    def "build config"() {
        given:
            Directory buildConfig = new Directory.Builder('', DirType.BUILD_CONFIG)
                    .parent(root)
                    .filename("build.gradle")
                    .text("apply plugin: 'java'")
                    .build()
        when:
            mainGenerator.generate(root)
        then:
            notThrown(Exception)
            exists(dirService.getFilePath(buildConfig))
    }
}
