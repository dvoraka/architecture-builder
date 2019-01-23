package dvoraka.archbuilder.generate

import com.fasterxml.jackson.databind.ObjectMapper
import dvoraka.archbuilder.Directory
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Ignore

class SerializationISpec extends BaseISpec {

    @Autowired
    ObjectMapper objectMapper


    @Ignore('WIP')
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
