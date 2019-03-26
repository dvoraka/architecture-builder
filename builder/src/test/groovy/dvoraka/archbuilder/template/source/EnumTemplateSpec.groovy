package dvoraka.archbuilder.template.source

import spock.lang.Specification

class EnumTemplateSpec extends Specification {

    def "test"() {
        setup:
            SourceTemplate template = new EnumTemplate(
                    'NewEnum', 'test.pkg', 'first', 'second')
            String source = template.getSource()

        expect:
            println source
            !source.isEmpty()
    }
}
