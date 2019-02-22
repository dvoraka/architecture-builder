package dvoraka.archbuilder.template

import com.fasterxml.jackson.databind.ObjectMapper
import dvoraka.archbuilder.BuildTool
import dvoraka.archbuilder.DirType
import dvoraka.archbuilder.Directory
import dvoraka.archbuilder.GradleBuildTool
import dvoraka.archbuilder.generate.Generator
import dvoraka.archbuilder.generate.JavaHelper
import dvoraka.archbuilder.generate.JavaTestingHelper
import dvoraka.archbuilder.generate.Utils
import dvoraka.archbuilder.sample.microservice.data.BaseException
import dvoraka.archbuilder.sample.microservice.data.ResultData
import dvoraka.archbuilder.sample.microservice.data.message.RequestMessage
import dvoraka.archbuilder.sample.microservice.data.message.ResponseMessage
import dvoraka.archbuilder.sample.microservice.server.AbstractServer
import dvoraka.archbuilder.sample.microservice.service.BaseService
import dvoraka.archbuilder.service.DirService
import dvoraka.archbuilder.springconfig.SpringConfigGenerator
import dvoraka.archbuilder.template.arch.MicroserviceTemplate
import groovy.json.JsonOutput
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Ignore
import spock.lang.Specification

@Slf4j
@SpringBootTest
class MicroserviceTemplateISpec extends Specification implements JavaHelper, JavaTestingHelper {

    @Autowired
    Generator mainGenerator
    @Autowired
    DirService dirService
    @Autowired
    ObjectMapper objectMapper
    @Autowired
    SpringConfigGenerator configGenerator

    String rootDirName = 'testing-service'
    String packageName = 'test.microservice'
    String serviceName = 'Budget'

    MicroserviceTemplate template
    Directory rootDir


    def setup() {

        template = new MicroserviceTemplate(
                rootDirName,
                packageName,
                BaseService.class,
                Collections.emptyList(),
                serviceName,
                BaseException.class,
                AbstractServer.class,
                ResultData.class,
                RequestMessage.class,
                ResponseMessage.class,
                configGenerator
        )

        rootDir = template.getRootDirectory()
    }

    def "create micro-service - budget service"() {
        when:
            mainGenerator.generate(rootDir)

        then:
            exists(DirType.SERVICE, rootDir, dirService)
            exists(DirType.SERVICE_ABSTRACT, rootDir, dirService)
            exists(DirType.SERVICE_IMPL, rootDir, dirService)

            exists(DirType.BUILD_CONFIG, rootDir, dirService)
            exists(DirType.SRC_PROPERTIES, rootDir, dirService)

        when:
            Directory serviceImplDir = dirService.findByType(DirType.SERVICE_IMPL, rootDir)
                    .get()
            Directory serviceDir = dirService.findByType(DirType.SERVICE, rootDir)
                    .get()
            Class<?> serviceImplClass = loadClass(defaultServiceImplName(serviceDir))

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
