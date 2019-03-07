package dvoraka.archbuilder

import spock.lang.Specification

class TextBuilderSpec extends Specification {

    TextBuilder builder


    def setup() {
        builder = new TextBuilder()
    }

    def "test"() {
        setup:
            builder
                    .addLine("line 1")
                    .addLine()
                    .addLine("line 3")
                    .addLine()
                    .addLine("line 5")
                    .addLine("line 6")
        expect:
            println builder.getText()
    }

    def "substitution test"() {
        setup:
            builder
                    .addLine('line 1')
                    .addLine()
                    .addLine('line 3 with variable 1: ${.var1}')
                    .addLine()
                    .addLine('line 5')
                    .addLine('line 6 with variable 2: ${$.var*} and variable 1: ${.var1}')
                    .variable('.var1', 'house$$')
                    .variable('$.var*', 'car*')
        expect:
            println 'original:'
            println builder.getText()
            println 'substituted:'
            println builder.render()
    }
}
