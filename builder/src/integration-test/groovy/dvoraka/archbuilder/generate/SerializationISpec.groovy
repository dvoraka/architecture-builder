package dvoraka.archbuilder.generate

import com.fasterxml.jackson.databind.ObjectMapper
import dvoraka.archbuilder.data.Directory
import dvoraka.archbuilder.service.DirService
import org.springframework.beans.factory.annotation.Autowired

class SerializationISpec extends BaseISpec {

    @Autowired
    ObjectMapper objectMapper
    @Autowired
    DirService dirService


    def "simple directory tree deserialization"() {
        given:
            Directory dir = srcRoot
            Directory rootDir = dirService.getRoot(dir)
        when:
            String json = objectMapper.writeValueAsString(rootDir)
            println json
        then:
            notThrown(Exception)
        when:
            Directory loadedDir = objectMapper.readValue(json, Directory)
        then:
            notThrown(Exception)
            loadedDir == rootDir
    }
}
