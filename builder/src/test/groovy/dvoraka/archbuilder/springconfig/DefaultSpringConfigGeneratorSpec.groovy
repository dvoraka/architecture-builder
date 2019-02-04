package dvoraka.archbuilder.springconfig

import com.squareup.javapoet.CodeBlock
import spock.lang.Ignore
import spock.lang.Specification

class DefaultSpringConfigGeneratorSpec extends Specification {

    DefaultSpringConfigGenerator generator = new DefaultSpringConfigGenerator()


    @Ignore('WIP')
    def "simple return"() {
        given:
            BeanMapping mapping = new BeanMapping.Builder('getBean')
                    .type(Number)
                    .toType(Long)
                    .build()
        when:
            CodeBlock code = generator.simpleReturn(mapping)
        then:
            println code.toString()
    }

    def "param return"() {
    }
}
