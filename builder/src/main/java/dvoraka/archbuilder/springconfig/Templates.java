package dvoraka.archbuilder.springconfig;

import com.squareup.javapoet.CodeBlock;

public interface Templates {

    static CodeBlock simpleReturn(BeanMapping beanMapping)
            throws ClassNotFoundException {

        return CodeBlock.of(
                "return new $T()",
                Class.forName(beanMapping.getToTypeDir().getTypeName())
        );
    }
}
