package dvoraka.archbuilder.generate

import dvoraka.archbuilder.DirType
import dvoraka.archbuilder.Directory
import groovy.util.logging.Slf4j
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Shared
import spock.lang.Specification

@Slf4j
@SpringBootTest
class BaseISpec extends Specification implements JavaHelper, JavaTestingHelper {

    @Shared
    String rootDirName = "testRootDir"

    Directory root
    Directory srcRoot
    Directory srcBase
    Directory srcBaseAbs


    def setup() {
        root = new Directory.Builder(rootDirName, DirType.ROOT)
                .parent(null)
                .build()
        srcRoot = new Directory.Builder("src/main/java", DirType.SRC_ROOT)
                .parent(root)
                .build()
        srcBase = new Directory.Builder("dvoraka/testapp", DirType.SRC_BASE)
                .parent(srcRoot)
                .build()
        srcBaseAbs = new Directory.Builder("dvoraka/diffapp", DirType.SRC_BASE_ABSTRACT)
                .parent(root)
                .build()
    }

    def cleanup() {
        removeFiles(rootDirName)
    }

    def "test"() {
        expect:
            true
    }

    void removeFiles(String rootDirName) {
        Utils.removeFiles(rootDirName)
    }
}
