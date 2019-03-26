package dvoraka.archbuilder.template.source

import spock.lang.Specification

class SpringBootAppTemplateSpec extends Specification {

    def "test"() {
        setup:
            SourceTemplate template = new SpringBootAppTemplate(
                    'NewClass', 'test.pkg')
            String source = template.getSource()

        expect:
            println source
            !source.isEmpty()
    }
}
