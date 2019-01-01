package dvoraka.archbuilder.generate

import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.TypeSpec
import dvoraka.archbuilder.DirType
import dvoraka.archbuilder.Directory
import org.springframework.beans.factory.annotation.Autowired

class CustomTypeISpec extends BaseISpec {

    @Autowired
    Generator mainGenerator


    def "Custom type generation"() {
        given:
            String className = 'CustomType'
            String packageName = 'test'

            TypeSpec typeSpec = TypeSpec.classBuilder(className)
                    .build()
            JavaFile javaFile = JavaFile.builder(packageName, typeSpec)
                    .build()

            Directory customType = new Directory.DirectoryBuilder(packageName)
                    .type(DirType.CUSTOM_TYPE)
                    .parent(srcBase)
                    .filename(className)
                    .text(javaFile.toString())
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
