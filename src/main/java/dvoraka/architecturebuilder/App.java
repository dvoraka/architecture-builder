package dvoraka.architecturebuilder;

import dvoraka.architecturebuilder.generate.Generator;
import dvoraka.architecturebuilder.generate.LangGenerator;
import dvoraka.architecturebuilder.service.DirService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class App {

    @Autowired
    private DirService dirService;
    @Autowired
    private LangGenerator langGenerator;
    @Autowired
    private Generator generator;


    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Bean
    public CommandLineRunner runner() {
        return args -> {

            Directory root = new Directory.DirectoryBuilder("rootDir")
                    .type(DirType.ROOT)
                    .parent(null)
                    .build();

            Directory srcRoot = new Directory.DirectoryBuilder("src/main/java")
                    .type(DirType.SRC_ROOT)
                    .parent(root)
                    .build();

            Directory srcTestRoot = new Directory.DirectoryBuilder("src/test/groovy")
                    .type(DirType.SRC_TEST_ROOT)
                    .parent(root)
                    .build();

            Directory srcBase = new Directory.DirectoryBuilder("dvoraka/testapp")
                    .type(DirType.SRC_BASE)
                    .parent(srcRoot)
                    .build();

            Directory srcProps = new Directory.DirectoryBuilder("src/main/resources")
                    .type(DirType.SRC_PROPERTIES)
                    .parent(root)
                    .filename("application.properties")
                    .text("prop1=value\nprop2=value2\n")
                    .build();

            Directory srcTestProps = new Directory.DirectoryBuilder("src/test/resources")
                    .type(DirType.SRC_PROPERTIES)
                    .parent(root)
                    .filename("application.properties")
                    .text("testProp1=value\ntestProp2=value2\n")
                    .build();

            Directory srcBaseAbs = new Directory.DirectoryBuilder("dvoraka/diffapp")
                    .type(DirType.SRC_BASE_ABSTRACT)
                    .parent(root)
                    .build();

            Directory srcTestBase = new Directory.DirectoryBuilder("dvoraka/testapp")
                    .type(DirType.SRC_TEST_BASE)
                    .parent(srcTestRoot)
                    .build();

            //
            // abstracts
            //
            Directory abstractMapService = new Directory.DirectoryBuilder("service")
                    .type(DirType.SERVICE_ABSTRACT)
                    .parent(srcBaseAbs)
                    .typeName("java.util.Map")
                    .build();

            Directory abstractRFService = new Directory.DirectoryBuilder("service")
                    .type(DirType.SERVICE_ABSTRACT)
                    .parent(srcBaseAbs)
                    .typeName("java.util.concurrent.RunnableFuture")
                    .build();


            Directory abstractList = new Directory.DirectoryBuilder("component")
                    .type(DirType.ABSTRACT)
                    .parent(srcBase)
                    .typeName("java.util.List")
                    .build();

            Directory abstractTimer = new Directory.DirectoryBuilder("component")
                    .type(DirType.ABSTRACT)
                    .parent(srcBase)
                    .typeName("java.util.Timer")
                    .build();

            Directory abstractObject = new Directory.DirectoryBuilder("component")
                    .type(DirType.ABSTRACT)
                    .parent(srcBase)
                    .typeName("java.lang.Object")
                    .build();

            Directory simpleInterface = new Directory.DirectoryBuilder("test")
                    .type(DirType.ABSTRACT)
                    .parent(srcBase)
                    .typeName("dvoraka.architecturebuilder.test.SimpleInterface")
                    .build();

            Directory interface4p = new Directory.DirectoryBuilder("test")
                    .type(DirType.ABSTRACT)
                    .parent(srcBase)
                    .typeName("dvoraka.architecturebuilder.test.Interface4P")
                    .build();

            Directory mapService = new Directory.DirectoryBuilder("service")
                    .type(DirType.SERVICE)
                    .parent(srcBase)
                    .superType(abstractMapService)
                    .filename("CoolMapService")
                    .parameterType("java.lang.String")
                    .parameterType("java.lang.Long")
                    .build();

            Directory rfService = new Directory.DirectoryBuilder("service")
                    .type(DirType.SERVICE)
                    .parent(srcBase)
                    .superType(abstractRFService)
                    .filename("RFService")
                    .parameterType("java.lang.String")
                    .build();

            //
            // implementations
            //
            Directory mapService1Impl = new Directory.DirectoryBuilder("service")
                    .type(DirType.SERVICE_IMPL)
                    .parent(srcBase)
                    .superType(mapService)
                    .build();

//            Directory rfService1Impl = new Directory.DirectoryBuilder("service2")
//                    .withType(DirType.SERVICE_IMPL)
//                    .withParent(srcBase)
//                    .withSuperType(rfService)
//                    .build();

            Directory listImpl = new Directory.DirectoryBuilder("component")
                    .type(DirType.IMPL)
                    .parent(srcBase)
                    .superType(abstractList)
                    .filename("CoolList")
                    .parameterType("java.lang.Integer")
                    .build();

            Directory objectImpl = new Directory.DirectoryBuilder("component")
                    .type(DirType.IMPL)
                    .parent(srcBase)
                    .superType(abstractObject)
                    .abstractType()
                    .filename("CoolObject")
                    .build();

            Directory simpleInterfaceImpl = new Directory.DirectoryBuilder("test")
                    .type(DirType.IMPL)
                    .parent(srcBase)
                    .superType(simpleInterface)
                    .abstractType()
                    .filename("DefaultSimpleInterface")
                    .build();

            Directory interface4pImpl = new Directory.DirectoryBuilder("test")
                    .type(DirType.IMPL)
                    .parent(srcBase)
                    .superType(interface4p)
                    .filename("DefaultInterface4P")
                    .parameterType("java.lang.String")
                    .parameterType("java.lang.Long")
                    .parameterType("java.lang.Boolean")
                    .parameterType("dvoraka.architecturebuilder.test.SimpleInterface")
                    .build();

            Directory timerImpl = new Directory.DirectoryBuilder("componentAux")
                    .type(DirType.IMPL)
                    .parent(srcBase)
                    .superType(abstractTimer)
                    .filename("CoolTimer")
                    .build();

            generator.generate(root);
        };
    }
}
