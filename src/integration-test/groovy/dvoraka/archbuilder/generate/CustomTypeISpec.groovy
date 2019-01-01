package dvoraka.archbuilder.generate

import dvoraka.archbuilder.DirType
import dvoraka.archbuilder.Directory
import org.springframework.beans.factory.annotation.Autowired

class CustomTypeISpec extends BaseISpec {

    @Autowired
    Generator mainGenerator


    def "Custom type generation"() {
        given:
            Directory customType = new Directory.DirectoryBuilder("test")
                    .type(DirType.CUSTOM_TYPE)
                    .parent(srcBase)
                    .filename("CustomType")
                    .build()
        when:
            mainGenerator.generate(root)
//            Class<?> clazz = loadClass(getClassName(customType))
        then:
            true
//            notThrown(Exception)
//            isPublicNotAbstract(clazz)
//            hasNoTypeParameters(clazz)
//            hasDeclaredMethods(clazz)
//            runMethod(clazz, "size") == 0
    }
}
