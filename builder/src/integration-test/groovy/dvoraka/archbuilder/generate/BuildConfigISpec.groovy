package dvoraka.archbuilder.generate

import dvoraka.archbuilder.data.DirType
import dvoraka.archbuilder.data.Directory
import dvoraka.archbuilder.service.DirService
import dvoraka.archbuilder.template.text.BuildGradleTemplate
import dvoraka.archbuilder.template.text.SettingsGradleTemplate
import dvoraka.archbuilder.template.text.TextFileTemplate
import org.springframework.beans.factory.annotation.Autowired

import java.nio.file.Files

class BuildConfigISpec extends BaseISpec {

    @Autowired
    Generator mainGenerator
    @Autowired
    DirService dirService


    def "Gradle build.gradle generation"() {
        given:
            BuildGradleTemplate template = new BuildGradleTemplate()
            template.plugins = ['java', 'groovy']

            Directory buildGradle = new Directory.Builder('', DirType.TEXT)
                    .parent(root)
                    .filename(template.getFilename())
                    .text(template.getText())
                    .build()
        when:
            mainGenerator.generate(root)
        then:
            notThrown(Exception)
            Files.exists(dirService.getFilePath(buildGradle))
    }

    def "Gradle settings.gradle generation"() {
        given:
            TextFileTemplate template = new SettingsGradleTemplate('TestProject1')

            Directory settingsGradle = new Directory.Builder('', DirType.TEXT)
                    .parent(root)
                    .filename(template.getFilename())
                    .text(template.getText())
                    .build()
        when:
            mainGenerator.generate(root)
        then:
            notThrown(Exception)
            Files.exists(dirService.getFilePath(settingsGradle))
    }
}
