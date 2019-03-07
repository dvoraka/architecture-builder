package dvoraka.archbuilder.template.source;

import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.lang.model.element.Modifier;

import static java.util.Objects.requireNonNull;

public class SpringBootAppTemplate implements SourceTemplate {

    public static final String ARGS_NAME = "args";
    private final String className;
    private final String packageName;


    public SpringBootAppTemplate(String className, String packageName) {
        this.className = requireNonNull(className);
        this.packageName = requireNonNull(packageName);
    }

    @Override
    public String getSource() {
        MethodSpec methodSpec = mainMethodSpec(ARGS_NAME);

        TypeSpec typeSpec = TypeSpec.classBuilder(getClassName())
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(SpringBootApplication.class)
                .addMethod(methodSpec)
                .build();

        JavaFile javaFile = JavaFile.builder(getPackageName(), typeSpec)
                .build();

        return javaFile.toString();
    }

    protected MethodSpec mainMethodSpec(String argsName) {
        return MethodSpec.methodBuilder("main")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(Void.TYPE)
                .addParameter(ArrayTypeName.of(String.class), argsName)
                .addStatement("$T.run($L.class, $L)", SpringApplication.class, getClassName(), argsName)
                .build();
    }

    protected String getClassName() {
        return className;
    }

    protected String getPackageName() {
        return packageName;
    }
}
