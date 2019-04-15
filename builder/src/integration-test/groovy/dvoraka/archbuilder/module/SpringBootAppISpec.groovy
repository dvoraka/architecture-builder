package dvoraka.archbuilder.module

import com.fasterxml.jackson.databind.ObjectMapper
import dvoraka.archbuilder.BuilderHelper
import dvoraka.archbuilder.BuilderProperties
import dvoraka.archbuilder.build.BuildTool
import dvoraka.archbuilder.build.GradleBuildTool
import dvoraka.archbuilder.data.DirType
import dvoraka.archbuilder.data.Directory
import dvoraka.archbuilder.generate.Generator
import dvoraka.archbuilder.generate.JavaHelper
import dvoraka.archbuilder.generate.JavaTestingHelper
import dvoraka.archbuilder.service.DirService
import dvoraka.archbuilder.springconfig.SpringConfigGenerator
import dvoraka.archbuilder.util.Utils
import groovy.json.JsonOutput
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Ignore
import spock.lang.Specification

@Slf4j
@SpringBootTest
class SpringBootAppISpec extends Specification implements JavaHelper, JavaTestingHelper {

    @Autowired
    Generator mainGenerator
    @Autowired
    DirService dirService
    @Autowired
    ObjectMapper objectMapper
    @Autowired
    BuilderProperties properties
    @Autowired
    SpringConfigGenerator configGenerator

    String rootDirName = 'trading-service'
    String packageName = 'test.trading'
    String appName = 'Trading'

    Module module
    Directory rootDir


    def setup() {
        BuilderHelper helper = new BuilderHelper(properties, rootDirName, packageName, appName)

        module = new SpringBootApp(helper)

        rootDir = module.getRootDirectory()
    }

    def "create Spring Boot app - trading app"() {
        when:
            mainGenerator.generate(rootDir)
        then:
            filesExist(dirService, rootDir)
        when:
            Directory bootAppDir = dirService.findByType(DirType.CUSTOM_TYPE, rootDir)
                    .get()
            Class<?> bootAppClass = loadClass(bootAppDir.getTypeName())
        then:
            bootAppClass.getSimpleName() == appName + 'App'
        when:
            BuildTool buildTool = new GradleBuildTool(new File(rootDirName))
            buildTool.prepareEnv()
        then:
            notThrown(Exception)
        cleanup:
            Utils.removeFiles(rootDirName)
    }

    @Ignore("needs working repository")
    def "create app with build"() {
        when:
            mainGenerator.generate(rootDir)
        then:
            notThrown(Exception)
        when:
            BuildTool buildTool = new GradleBuildTool(new File(rootDirName))
            buildTool.prepareEnv()
        then:
            notThrown(Exception)
        when:
            buildTool.build()
        then:
            notThrown(Exception)
        cleanup:
            Utils.removeFiles(rootDirName)
            true
    }

    def "app directory serialization"() {
        when:
            String json = objectMapper.writeValueAsString(rootDir)
            println JsonOutput.prettyPrint(json)
        then:
            notThrown(Exception)
    }

    def "app directory deserialization"() {
        when:
            String json = objectMapper.writeValueAsString(rootDir)
        then:
            notThrown(Exception)
        when:
            Directory loadedDir = objectMapper.readValue(json, Directory)
        then:
            notThrown(Exception)
            loadedDir == rootDir
    }
}
