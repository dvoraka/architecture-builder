package dvoraka.archbuilder.template.source;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import dvoraka.archbuilder.exception.GeneratorException;
import dvoraka.archbuilder.util.JavaUtils;

import javax.lang.model.element.Modifier;

import static java.util.Objects.requireNonNull;

public class EnumTemplate implements SourceTemplate {

    private final String enumName;
    private final String packageName;
    private final String[] constants;


    public EnumTemplate(String enumName, String packageName, String... constants) {
        if (constants.length == 0) {
            throw new GeneratorException("No constants for enum.");
        }

        this.enumName = requireNonNull(enumName);
        this.packageName = requireNonNull(packageName);
        this.constants = constants;
    }

    @Override
    public String getSource() {
        TypeSpec.Builder builder = TypeSpec.enumBuilder(enumName)
                .addModifiers(Modifier.PUBLIC);

        for (String constant : constants) {
            builder.addEnumConstant(constant.toUpperCase());
        }

        JavaFile javaFile = JavaFile.builder(getPackageName(), builder.build())
                .build();

        return javaFile.toString();
    }

    @Override
    public String getTypeName() {
        return enumName;
    }

    @Override
    public String getPackageName() {
        return packageName;
    }

    @Override
    public String getFilename() {
        return JavaUtils.javaSuffix(getTypeName());
    }

    @Override
    public String getPath() {
        return "";
    }
}
