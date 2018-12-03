package dvoraka.archbuilder.generate


import org.springframework.beans.factory.annotation.Autowired

class ServiceImplementationISpec extends BaseISpec {

    @Autowired
    Generator mainGenerator


    def "test"() {
        expect:
            true
    }
}
