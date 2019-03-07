package dvoraka.archbuilder.template.source;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

import javax.lang.model.element.Modifier;

public class SpringBootApp2Template extends SpringBootAppTemplate {

    public SpringBootApp2Template(String className, String packageName) {
        super(className, packageName);
    }

    @Override
    public String getSource() {
        MethodSpec methodSpec = mainMethodSpec(ARGS_NAME);

        TypeSpec typeSpec = TypeSpec.classBuilder(getClassName())
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(SpringBootConfiguration.class)
                .addAnnotation(EnableAutoConfiguration.class)
                .addMethod(methodSpec)
                .build();

        JavaFile javaFile = JavaFile.builder(getPackageName(), typeSpec)
                .build();

        return javaFile.toString();
    }
}
