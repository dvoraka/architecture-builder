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
                    .withType(DirType.ROOT)
                    .withParent(null)
                    .build();

            Directory srcRoot = new Directory.DirectoryBuilder("src/main/java")
                    .withType(DirType.SRC_ROOT)
                    .withParent(root)
                    .build();

            Directory srcTestRoot = new Directory.DirectoryBuilder("src/test/groovy")
                    .withType(DirType.SRC_TEST_ROOT)
                    .withParent(root)
                    .build();

            Directory srcBase = new Directory.DirectoryBuilder("dvoraka/testapp")
                    .withType(DirType.SRC_BASE)
                    .withParent(srcRoot)
                    .build();

            Directory srcProps = new Directory.DirectoryBuilder("src/main/resources")
                    .withType(DirType.SRC_PROPERTIES)
                    .withParent(root)
                    .withFilename("application.properties")
                    .build();

            Directory srcTestProps = new Directory.DirectoryBuilder("src/test/resources")
                    .withType(DirType.SRC_PROPERTIES)
                    .withParent(root)
                    .withFilename("application.properties")
                    .build();

            Directory srcBaseAbs = new Directory.DirectoryBuilder("dvoraka/diffapp")
                    .withType(DirType.SRC_BASE_ABSTRACT)
                    .withParent(root)
                    .build();

            Directory srcTestBase = new Directory.DirectoryBuilder("dvoraka/testapp")
                    .withType(DirType.SRC_TEST_BASE)
                    .withParent(srcTestRoot)
                    .build();

            //
            // abstracts
            //
            Directory abstractMapService = new Directory.DirectoryBuilder("service")
                    .withType(DirType.SERVICE_ABSTRACT)
                    .withParent(srcBaseAbs)
                    .withTypeName("java.util.Map")
                    .build();

            Directory abstractRFService = new Directory.DirectoryBuilder("service")
                    .withType(DirType.SERVICE_ABSTRACT)
                    .withParent(srcBaseAbs)
                    .withTypeName("java.util.concurrent.RunnableFuture")
                    .build();


            Directory abstractList = new Directory.DirectoryBuilder("component")
                    .withType(DirType.ABSTRACT)
                    .withParent(srcBase)
                    .withTypeName("java.util.List")
                    .build();

            Directory abstractTimer = new Directory.DirectoryBuilder("component")
                    .withType(DirType.ABSTRACT)
                    .withParent(srcBase)
                    .withTypeName("java.util.Timer")
                    .build();

            Directory abstractObject = new Directory.DirectoryBuilder("component")
                    .withType(DirType.ABSTRACT)
                    .withParent(srcBase)
                    .withTypeName("java.lang.Object")
                    .build();

            Directory mapService = new Directory.DirectoryBuilder("service")
                    .withType(DirType.SERVICE)
                    .withParent(srcBase)
                    .withSuperType(abstractMapService)
                    .withFilename("CoolMapService")
                    .addParameterType("java.lang.String")
                    .addParameterType("java.lang.Long")
                    .build();

            Directory rfService = new Directory.DirectoryBuilder("service")
                    .withType(DirType.SERVICE)
                    .withParent(srcBase)
                    .withSuperType(abstractRFService)
                    .withFilename("RFService")
                    .addParameterType("java.lang.String")
                    .build();

            //
            // implementations
            //
            Directory mapService1Impl = new Directory.DirectoryBuilder("service")
                    .withType(DirType.SERVICE_IMPL)
                    .withParent(srcBase)
                    .withSuperType(mapService)
                    .build();

            Directory rfService1Impl = new Directory.DirectoryBuilder("service2")
                    .withType(DirType.SERVICE_IMPL)
                    .withParent(srcBase)
                    .withSuperType(rfService)
                    .build();

            Directory listImp = new Directory.DirectoryBuilder("component")
                    .withType(DirType.IMPL)
                    .withParent(srcBase)
                    .withSuperType(abstractList)
                    .withFilename("CoolList")
                    .addParameterType("java.lang.Integer")
                    .build();

            generator.generate(root);
        };
    }
}
