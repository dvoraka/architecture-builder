package dvoraka.archbuilder.generate

import dvoraka.archbuilder.DirType
import dvoraka.archbuilder.Directory
import dvoraka.archbuilder.service.DirService
import dvoraka.archbuilder.template.config.BuildGradleTemplate
import org.springframework.beans.factory.annotation.Autowired

import java.nio.file.Files

class BuildConfigISpec extends BaseISpec {

    @Autowired
    Generator mainGenerator
    @Autowired
    DirService dirService


    def "Gradle build configuration generation"() {
        given:
            BuildGradleTemplate template = new BuildGradleTemplate()
            template.plugins = ['java', 'groovy']

            Directory gradleConfig = new Directory.DirectoryBuilder('')
                    .type(DirType.BUILD_CONFIG)
                    .parent(root)
                    .filename(template.getFilename())
                    .text(template.getConfig())
                    .build()
        when:
            mainGenerator.generate(root)
        then:
            notThrown(Exception)
            Files.exists(dirService.getFilePath(gradleConfig))
    }
}
