package dvoraka.archbuilder.generate

import com.squareup.javapoet.ArrayTypeName
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeSpec
import dvoraka.archbuilder.DirType
import dvoraka.archbuilder.Directory
import dvoraka.archbuilder.template.source.SourceTemplate
import dvoraka.archbuilder.template.source.SpringBootApplicationTemplate
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
            declaredMethodCount(clazz) == 1
    }

    def "Custom type with source template"() {
        given:
            String className = 'CoolApplication'
            String path = srcBase.getPackageName()
            String packageName = JavaUtils.path2pkg(path)

            SourceTemplate template = new SpringBootApplicationTemplate(
                    className,
                    packageName
            )

            Directory customType = new Directory.DirectoryBuilder('')
                    .type(DirType.CUSTOM_TYPE)
                    .parent(srcBase)
                    .filename(className)
                    .text(template.getSource())
                    .build()
        when:
            mainGenerator.generate(root)
            Class<?> clazz = loadClass(getClassName(customType))
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