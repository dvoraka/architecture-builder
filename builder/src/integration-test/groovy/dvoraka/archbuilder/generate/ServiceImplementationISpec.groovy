package dvoraka.archbuilder.generate

import dvoraka.archbuilder.data.DirType
import dvoraka.archbuilder.data.Directory
import dvoraka.archbuilder.sample.generic.ServiceInterface2p
import org.springframework.beans.factory.annotation.Autowired

class ServiceImplementationISpec extends BaseISpec {

    @Autowired
    Generator mainGenerator


    def "Map service implementation"() {
        given:
            Directory abstractMapService = new Directory.Builder('service', DirType.SERVICE_ABSTRACT)
                    .parent(srcBaseAbs)
                    .typeName('java.util.Map')
                    .build()
            Directory mapService = new Directory.Builder('service', DirType.SERVICE)
                    .parent(srcBase)
                    .superType(abstractMapService)
                    .filename('CoolMapService')
                    .parameterTypeName('java.lang.String')
                    .parameterTypeName('java.lang.Long')
                    .build()
            Directory mapServiceImpl = new Directory.Builder('service', DirType.SERVICE_IMPL)
                    .parent(srcBase)
                    .superType(mapService)
                    .filename('DefaultCoolMapService')
                    .build()
        when:
            mainGenerator.generate(root)
            Class<?> clazz = loadClass(defaultServiceImplName(mapService))
        then:
            notThrown(Exception)
            isPublicNotAbstract(clazz)
            hasNoTypeParameters(clazz)
            hasDeclaredMethods(clazz)
    }

    def "RunnableFuture service implementation"() {
        given:
            Directory abstractRFService = new Directory.Builder("service", DirType.SERVICE_ABSTRACT)
                    .parent(srcBaseAbs)
                    .typeName("java.util.concurrent.RunnableFuture")
                    .build()
            Directory rfService = new Directory.Builder("service", DirType.SERVICE)
                    .parent(srcBase)
                    .superType(abstractRFService)
                    .filename("RFService")
                    .parameterTypeName("java.lang.String")
                    .build()
            Directory rfServiceImpl = new Directory.Builder("service2", DirType.SERVICE_IMPL)
                    .parent(srcBase)
                    .superType(rfService)
                    .filename('DefaultRFService')
                    .build()
        when:
            mainGenerator.generate(root)
        then:
            notThrown(Exception)
    }

    def "ServiceInterface2p service implementation"() {
        given:
            Class<?> cls = ServiceInterface2p.class
            Directory baseService = new Directory.Builder("service", DirType.SERVICE_ABSTRACT)
                    .parent(srcBaseAbs)
                    .typeClass(cls)
                    .build()
            Directory service = new Directory.Builder("service", DirType.SERVICE)
                    .parent(srcBase)
                    .superType(baseService)
                    .filename("Service")
                    .parameterTypeClass(String.class)
                    .parameterTypeClass(Long.class)
                    .build()
            Directory serviceImpl = new Directory.Builder("service", DirType.SERVICE_IMPL)
                    .parent(srcBase)
                    .superType(service)
                    .filename('DefaultService')
                    .build()
        when:
            mainGenerator.generate(root)
        then:
            notThrown(Exception)
    }
}
