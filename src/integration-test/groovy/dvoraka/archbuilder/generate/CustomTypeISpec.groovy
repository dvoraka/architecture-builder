package dvoraka.archbuilder.generate

import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.TypeSpec
import dvoraka.archbuilder.DirType
import dvoraka.archbuilder.Directory
import org.springframework.beans.factory.annotation.Autowired

import javax.lang.model.element.Modifier

class CustomTypeISpec extends BaseISpec {

    @Autowired
    Generator mainGenerator


    def "Custom type generation"() {
        given:
            String className = 'CustomType'
            String packagePath = 'test'
            String path = srcBase.getPackageName() + '/' + packagePath
            String packageName = JavaUtils.path2pkg(path)

            TypeSpec typeSpec = TypeSpec.classBuilder(className)
                    .addModifiers(Modifier.PUBLIC)
                    .build()
            JavaFile javaFile = JavaFile.builder(packageName, typeSpec)
                    .build()

            Directory customType = new Directory.DirectoryBuilder(packagePath)
                    .type(DirType.CUSTOM_TYPE)
                    .parent(srcBase)
                    .filename(className)
                    .text(javaFile.toString())
                    .build()
        when:
            mainGenerator.generate(root)
            Class<?> clazz = loadClass(getClassName(customType))
        then:
            notThrown(Exception)
            isPublicNotAbstract(clazz)
            hasNoTypeParameters(clazz)
            hasNoDeclaredMethods(clazz)
//            runMethod(clazz, "size") == 0
    }
}
