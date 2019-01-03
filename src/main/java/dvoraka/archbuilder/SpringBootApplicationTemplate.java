package dvoraka.archbuilder;

import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.lang.model.element.Modifier;

import static java.util.Objects.requireNonNull;

public class SpringBootApplicationTemplate implements SourceTemplate {

    private final String className;
    private final String packageName;


    public SpringBootApplicationTemplate(String className, String packageName) {
        this.className = requireNonNull(className);
        this.packageName = requireNonNull(packageName);
    }

    @Override
    public String getSource() {
        String argsName = "args";
        // public static void main
        MethodSpec methodSpec = MethodSpec.methodBuilder("main")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(Void.TYPE)
                .addParameter(ArrayTypeName.of(String.class), argsName)
                .addStatement("$T.run($L.class, $L)", SpringApplication.class, getClassName(), argsName)
                .build();
        TypeSpec typeSpec = TypeSpec.classBuilder(getClassName())
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(SpringBootApplication.class)
                .addMethod(methodSpec)
                .build();
        JavaFile javaFile = JavaFile.builder(getPackageName(), typeSpec)
                .build();

        return javaFile.toString();
    }

    private String getClassName() {
        return className;
    }

    private String getPackageName() {
        return packageName;
    }
}
