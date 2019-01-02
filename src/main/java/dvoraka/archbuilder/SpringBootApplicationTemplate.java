package dvoraka.archbuilder;

import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.lang.model.element.Modifier;

public class SpringBootApplicationTemplate implements SourceTemplate {

    //TODO

    @Override
    public String getSource() {

        String className = "CustomType";
        String packageName = "";
        String argsName = "args";

        // public static void main
        MethodSpec methodSpec = MethodSpec.methodBuilder("main")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(Void.TYPE)
                .addParameter(ArrayTypeName.of(String.class), argsName)
                .addStatement("$T.run($L.class, $L)", SpringApplication.class, className, argsName)
                .build();
        TypeSpec typeSpec = TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(SpringBootApplication.class)
                .addMethod(methodSpec)
                .build();
        JavaFile javaFile = JavaFile.builder(packageName, typeSpec)
                .build();

        return javaFile.toString();
    }
}
