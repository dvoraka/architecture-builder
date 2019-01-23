package dvoraka.archbuilder.generate

import com.fasterxml.jackson.databind.ObjectMapper
import dvoraka.archbuilder.Directory
import org.springframework.beans.factory.annotation.Autowired

class SerializationISpec extends BaseISpec {

    @Autowired
    ObjectMapper objectMapper


    def "simple directory tree deserialization"() {
        when:
            String json = objectMapper.writeValueAsString(srcRoot)
            println json
        then:
            notThrown(Exception)
        when:
            objectMapper.readValue(json, Directory)
        then:
            notThrown(Exception)
    }
}
