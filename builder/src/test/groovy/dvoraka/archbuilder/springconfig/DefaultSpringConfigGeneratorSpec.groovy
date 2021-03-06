package dvoraka.archbuilder.springconfig

import com.squareup.javapoet.CodeBlock
import dvoraka.archbuilder.data.DirType
import dvoraka.archbuilder.data.Directory
import spock.lang.Specification

class DefaultSpringConfigGeneratorSpec extends Specification {

    DefaultSpringConfigGenerator generator = new DefaultSpringConfigGenerator()


    def "simple return with class type"() {
        given:
            BeanMapping mapping = new BeanMapping.Builder('getBean')
                    .type(Number)
                    .toType(Long)
                    .build()
        when:
            CodeBlock code = generator.simpleReturn(mapping)
        then:
            code.toString() == 'return new java.lang.Long()'
    }

    def "simple return with dir type"() {
        given:
            Directory fromTypeDir = new Directory.Builder('test', DirType.ABSTRACT)
                    .parent(null)
                    .typeClass(Number)
                    .build()
            Directory toTypeDir = new Directory.Builder('test', DirType.ABSTRACT)
                    .parent(null)
                    .typeClass(Long)
                    .build()

            BeanMapping mapping = new BeanMapping.Builder('getBean')
                    .typeDir(fromTypeDir)
                    .toTypeDir(toTypeDir)
                    .build()
        when:
            CodeBlock code = generator.simpleReturn(mapping)
        then:
            code.toString() == 'return new java.lang.Long()'
    }

    def "param return"() {
        given:
            Directory fromTypeDir = new Directory.Builder('test', DirType.ABSTRACT)
                    .parent(null)
                    .typeClass(Number)
                    .build()
            Directory toTypeDir = new Directory.Builder('test', DirType.ABSTRACT)
                    .parent(null)
                    .typeClass(Long)
                    .build()

            // parameters
            BeanParameter strParam = new BeanParameter.Builder('str')
                    .type(String)
                    .build()
            BeanParameter longParam = new BeanParameter.Builder('value')
                    .type(Long)
                    .build()
            BeanParameter objParam = new BeanParameter.Builder('obj')
                    .type(Object)
                    .build()

            BeanMapping mapping = new BeanMapping.Builder('getBean')
                    .typeDir(fromTypeDir)
                    .toTypeDir(toTypeDir)
                    .addParameter(strParam)
                    .addParameter(longParam)
                    .addParameter(objParam)
                    .build()
        when:
            CodeBlock code = generator.paramReturn(mapping)
        then:
            code.toString() == 'return new java.lang.Long(str, value, obj)'
    }
}
