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

    def cleanup() {
//        removeFiles(rootDirName)
    }

    def "test"() {
        expect:
            true
    }

    def "create microservice"() {
        setup:
            MicroserviceTemplate template = new MicroserviceTemplate(
                    "testing-service",
                    "test.service",
                    ServiceInterface2p.class,
                    "TestingService"
            )

        expect:
            true
    }
}
