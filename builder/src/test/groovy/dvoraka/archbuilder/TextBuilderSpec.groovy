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
}
