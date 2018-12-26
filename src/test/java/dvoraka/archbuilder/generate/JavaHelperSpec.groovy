package dvoraka.archbuilder.generate

import dvoraka.archbuilder.test.microservice.data.message.ResponseMessage
import spock.lang.Specification

class JavaHelperSpec extends Specification {

    JavaHelper helper = Spy()


    def "is constructor needed"() {
        expect:
            !helper.isConstructorNeeded(List.class)
            !helper.isConstructorNeeded(Object.class)
            helper.isConstructorNeeded(ResponseMessage.class)
    }
}
