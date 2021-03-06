package dvoraka.archbuilder.module.microservice

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
import dvoraka.archbuilder.module.Module
import dvoraka.archbuilder.module.microservice.DefaultMicroservice
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
class DefaultMicroserviceISpec extends Specification implements JavaHelper, JavaTestingHelper {

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

    String rootDirName = 'budget-service'
    String packageName = 'test.budget'
    String serviceName = 'Budget'

    Module module
    Directory rootDir


    def setup() {
        BuilderHelper helper = new BuilderHelper(properties, rootDirName, packageName, serviceName)

        module = new DefaultMicroservice(helper, configGenerator)

        rootDir = module.getRootDirectory()
    }

    def "create micro-service - budget service"() {
        when:
            mainGenerator.generate(rootDir)
        then:
            filesExist(dirService, rootDir)
        and:
            exists(DirType.SERVICE, rootDir, dirService)
            exists(DirType.SERVICE_IMPL, rootDir, dirService)
            exists(DirType.SPRING_CONFIG, rootDir, dirService)
            exists(DirType.TEXT, rootDir, dirService)
        when:
            Directory serviceImplDir = dirService.findByType(DirType.SERVICE_IMPL, rootDir)
                    .get()
            Directory serviceDir = dirService.findByType(DirType.SERVICE, rootDir)
                    .get()
            Class<?> serviceImplClass = loadClass(serviceImplDir.getTypeName())
        then:
            serviceImplClass.getSimpleName() == 'Default' + serviceName + 'Service'
            serviceImplClass.getName() == defaultServiceImplName(serviceDir)
        when:
            BuildTool buildTool = new GradleBuildTool(new File(rootDirName))
            buildTool.prepareEnv()
        then:
            notThrown(Exception)
        cleanup:
            Utils.removeFiles(rootDirName)
    }

    @Ignore("needs working repository")
    def "create micro-service with build"() {
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

    def "micro-service directory serialization"() {
        when:
            String json = objectMapper.writeValueAsString(rootDir)
            println JsonOutput.prettyPrint(json)
        then:
            notThrown(Exception)
    }

    def "micro-service directory deserialization"() {
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
