package dvoraka.archbuilder.template

import dvoraka.archbuilder.generate.Generator
import dvoraka.archbuilder.generate.JavaTestingHelper
import dvoraka.archbuilder.service.DirService
import dvoraka.archbuilder.test.ServiceInterface2p
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@Slf4j
@SpringBootTest
class MicroserviceTemplateISpec extends Specification implements JavaTestingHelper {

    @Autowired
    Generator mainGenerator
    @Autowired
    DirService dirService


    def setup() {
    }

    def "test"() {
        expect:
            true
    }

    def "create microservice"() {
        given:
            String rootDirName = "testing-service"
            MicroserviceTemplate template = new MicroserviceTemplate(
                    rootDirName,
                    "test.testservice",
                    ServiceInterface2p.class,
                    "TestingService"
            )

        when:
            mainGenerator.generate(template.getRootDirectory())

        then:
            true

        cleanup:
            removeFiles(rootDirName)
            true
    }
}
