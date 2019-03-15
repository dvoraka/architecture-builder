package dvoraka.archbuilder.generate

import com.squareup.javapoet.ArrayTypeName
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeSpec
import dvoraka.archbuilder.data.DirType
import dvoraka.archbuilder.data.Directory
import dvoraka.archbuilder.template.source.SourceTemplate
import dvoraka.archbuilder.template.source.SpringBootAppTemplate
import dvoraka.archbuilder.util.JavaUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

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
            String argsName = 'args'

            MethodSpec methodSpec = MethodSpec.methodBuilder('main')
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .returns(Void.TYPE)
                    .addParameter(ArrayTypeName.of(String.class), argsName)
                    .addStatement('$T.run($L.class, $L)', SpringApplication.class, className, argsName)
                    .build()
            TypeSpec typeSpec = TypeSpec.classBuilder(className)
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(SpringBootApplication.class)
                    .addMethod(methodSpec)
                    .build()
            JavaFile javaFile = JavaFile.builder(packageName, typeSpec)
                    .build()

            Directory customType = new Directory.Builder(packagePath, DirType.CUSTOM_TYPE)
                    .parent(srcBase)
                    .filename(javaSuffix(className))
                    .text(javaFile.toString())
                    .build()
        when:
            mainGenerator.generate(root)
            Class<?> clazz = loadClass(customType.getTypeName())
        then:
            notThrown(Exception)
            isPublicNotAbstract(clazz)
            hasNoTypeParameters(clazz)
            declaredMethodCount(clazz) == 1
    }

    def "Custom type with source template"() {
        given:
            String className = 'CoolApplication'
            String path = srcBase.getPackageName()
            String packageName = JavaUtils.path2pkg(path)

            SourceTemplate template = new SpringBootAppTemplate(
                    className,
                    packageName
            )

            Directory customType = new Directory.Builder('', DirType.CUSTOM_TYPE)
                    .parent(srcBase)
                    .filename(javaSuffix(className))
                    .text(template.getSource())
                    .build()
        when:
            mainGenerator.generate(root)
            Class<?> clazz = loadClass(customType.getTypeName())
        then:
            notThrown(Exception)
            isPublicNotAbstract(clazz)
            hasNoTypeParameters(clazz)
            declaredMethodCount(clazz) == 1
        when:
            runMainMethod(clazz, new String[0])
        then:
            notThrown(Exception)
    }
}
