package dvoraka.archbuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import dvoraka.archbuilder.generate.Generator;
import dvoraka.archbuilder.generate.LangGenerator;
import dvoraka.archbuilder.generate.MainGenerator;
import dvoraka.archbuilder.sample.microservice.data.BaseException;
import dvoraka.archbuilder.sample.microservice.data.ResultData;
import dvoraka.archbuilder.sample.microservice.data.message.RequestMessage;
import dvoraka.archbuilder.sample.microservice.data.message.ResponseMessage;
import dvoraka.archbuilder.sample.microservice.net.BaseNetComponent;
import dvoraka.archbuilder.sample.microservice.net.ServiceNetComponent;
import dvoraka.archbuilder.sample.microservice.net.receive.NetReceiver;
import dvoraka.archbuilder.sample.microservice.server.AbstractServer;
import dvoraka.archbuilder.sample.microservice.service.BaseService;
import dvoraka.archbuilder.service.DirService;
import dvoraka.archbuilder.springconfig.SpringConfigGenerator;
import dvoraka.archbuilder.template.arch.MicroserviceTemplate;
import dvoraka.archbuilder.template.arch.NetTemplateConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import java.io.File;
import java.util.Collections;

@SpringBootApplication
public class App {

    @Autowired
    private Generator mainGenerator;
    @Autowired
    private SpringConfigGenerator configGenerator;


    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Profile("!test")
    @Bean
    public CommandLineRunner runner() {
        return args -> {
            System.out.println("App");

            // micro-service template testing
            String rootDirName = "budget-service";
            String packageName = "test.budget";
            String serviceName = "Budget";

            NetTemplateConfig netTemplateConfig = new NetTemplateConfig(
                    ResultData.class,
                    RequestMessage.class,
                    ResponseMessage.class,
                    ServiceNetComponent.class,
                    NetReceiver.class,
                    BaseNetComponent.class
            );

            MicroserviceTemplate template = new MicroserviceTemplate(
                    rootDirName,
                    packageName,
                    BaseService.class,
                    Collections.emptyList(),
                    serviceName,
                    BaseException.class,
                    AbstractServer.class,
                    netTemplateConfig,
                    configGenerator
            );

            mainGenerator.generate(template.getRootDirectory());

            BuildTool buildTool = new GradleBuildTool(new File(rootDirName));
            buildTool.prepareEnv();
        };
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new Jdk8Module());

        return objectMapper;
    }

    @Profile("test")
    @Bean
    public Generator mainGenerator(DirService dirService, LangGenerator langGenerator) {
        MainGenerator mainGenerator = new MainGenerator(dirService, langGenerator);
        mainGenerator.setRemoveClasses(false);

        return mainGenerator;
    }
}
