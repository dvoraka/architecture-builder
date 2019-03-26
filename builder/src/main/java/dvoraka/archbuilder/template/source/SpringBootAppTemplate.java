package dvoraka.archbuilder.template.source;

import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import dvoraka.archbuilder.util.JavaUtils;
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

    protected MethodSpec mainMethodSpec(String argsName) {
        return MethodSpec.methodBuilder("main")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(Void.TYPE)
                .addParameter(ArrayTypeName.of(String.class), argsName)
                .addStatement("$T.run($L.class, $L)", SpringApplication.class, getClassName(), argsName)
                .build();
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

    @Override
    public String getTypeName() {
        return getClassName();
    }

    protected String getClassName() {
        return className;
    }

    @Override
    public String getPackageName() {
        return packageName;
    }

    @Override
    public String getFilename() {
        return JavaUtils.javaSuffix(getClassName());
    }

    @Override
    public String getText() {
        return getSource();
    }

    @Override
    public String getPath() {
        return "";
    }
}
