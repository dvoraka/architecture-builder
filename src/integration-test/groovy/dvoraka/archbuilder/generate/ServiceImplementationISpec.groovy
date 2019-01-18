package dvoraka.archbuilder.generate

import dvoraka.archbuilder.DirType
import dvoraka.archbuilder.Directory
import dvoraka.archbuilder.sample.ServiceInterface2p
import org.springframework.beans.factory.annotation.Autowired

class ServiceImplementationISpec extends BaseISpec {

    @Autowired
    Generator mainGenerator


    def "Map service implementation"() {
        given:
            Directory abstractMapService = new Directory.DirectoryBuilder("service")
                    .type(DirType.SERVICE_ABSTRACT)
                    .parent(srcBaseAbs)
                    .typeName("java.util.Map")
                    .build()
            Directory mapService = new Directory.DirectoryBuilder("service")
                    .type(DirType.SERVICE)
                    .parent(srcBase)
                    .superType(abstractMapService)
                    .filename("CoolMapService")
                    .parameterTypeName("java.lang.String")
                    .parameterTypeName("java.lang.Long")
                    .build()
            Directory mapServiceImpl = new Directory.DirectoryBuilder("service")
                    .type(DirType.SERVICE_IMPL)
                    .parent(srcBase)
                    .superType(mapService)
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
            Directory abstractRFService = new Directory.DirectoryBuilder("service")
                    .type(DirType.SERVICE_ABSTRACT)
                    .parent(srcBaseAbs)
                    .typeName("java.util.concurrent.RunnableFuture")
                    .build()
            Directory rfService = new Directory.DirectoryBuilder("service")
                    .type(DirType.SERVICE)
                    .parent(srcBase)
                    .superType(abstractRFService)
                    .filename("RFService")
                    .parameterTypeName("java.lang.String")
                    .build()
            Directory rfServiceImpl = new Directory.DirectoryBuilder("service2")
                    .type(DirType.SERVICE_IMPL)
                    .parent(srcBase)
                    .superType(rfService)
                    .build()
        when:
            mainGenerator.generate(root)
        then:
            notThrown(Exception)
    }

    def "ServiceInterface2p service implementation"() {
        given:
            Directory baseService = new Directory.DirectoryBuilder("service")
                    .type(DirType.SERVICE_ABSTRACT)
                    .parent(srcBaseAbs)
                    .typeClass(ServiceInterface2p)
                    .build()
            Directory service = new Directory.DirectoryBuilder("service")
                    .type(DirType.SERVICE)
                    .parent(srcBase)
                    .superType(baseService)
                    .filename("Service")
                    .parameterTypeClass(String.class)
                    .parameterTypeClass(Long.class)
                    .build()
            Directory serviceImpl = new Directory.DirectoryBuilder("service")
                    .type(DirType.SERVICE_IMPL)
                    .parent(srcBase)
                    .superType(service)
                    .build()
        when:
            mainGenerator.generate(root)
        then:
            notThrown(Exception)
    }
}
