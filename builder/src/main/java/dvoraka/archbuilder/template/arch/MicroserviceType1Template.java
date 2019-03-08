package dvoraka.archbuilder.template.arch;

import dvoraka.archbuilder.Directory;
import dvoraka.archbuilder.sample.microservice.data.BaseException;
import dvoraka.archbuilder.sample.microservice.data.ResultData;
import dvoraka.archbuilder.sample.microservice.data.message.RequestMessage;
import dvoraka.archbuilder.sample.microservice.data.message.ResponseMessage;
import dvoraka.archbuilder.sample.microservice.net.BaseNetComponent;
import dvoraka.archbuilder.sample.microservice.net.ServiceNetComponent;
import dvoraka.archbuilder.sample.microservice.net.receive.NetReceiver;
import dvoraka.archbuilder.sample.microservice.server.AbstractServer;
import dvoraka.archbuilder.sample.microservice.service.BaseService;
import dvoraka.archbuilder.springconfig.SpringConfigGenerator;

import java.util.Collections;

public class MicroserviceType1Template implements ArchitectureTemplate {

    private Directory root;


    public MicroserviceType1Template(
            String rootDirName,
            String packageName,
            String serviceName,
            SpringConfigGenerator configGenerator
    ) {
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

        root = template.getRootDirectory();
    }

    @Override
    public Directory getRootDirectory() {
        return root;
    }
}
