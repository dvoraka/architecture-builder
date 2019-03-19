package dvoraka.archbuilder.submodule;

import dvoraka.archbuilder.data.DirType;
import dvoraka.archbuilder.data.Directory;
import dvoraka.archbuilder.exception.GeneratorException;
import dvoraka.archbuilder.springconfig.BeanMapping;
import dvoraka.archbuilder.springconfig.SpringConfigGenerator;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static dvoraka.archbuilder.module.ConfigurableMicroservice.MESSAGE_DIR;
import static dvoraka.archbuilder.util.Utils.uncapitalize;

public class NetSubmodule implements JavaSubmodule {

    private final String baseName;
    private final Directory service;
    private final NetConfig config;

    private final SpringConfigGenerator configGenerator;

    private final List<BeanMapping> configuration;


    public NetSubmodule(
            String baseName,
            Directory service,
            NetConfig config,
            SpringConfigGenerator configGenerator
    ) {
        this.baseName = baseName;
        this.service = service;
        this.config = config;
        this.configGenerator = configGenerator;

        configuration = new ArrayList<>();
    }

    @Override
    public void addSubmoduleTo(Directory srcBase) {

        if (!srcBase.isBase()) {
            throw new GeneratorException("Src base must be base.");
        }

        // exception
        String exceptionName = baseName + "Exception";
        Directory exception = new Directory.Builder("exception", DirType.IMPL)
                .parent(srcBase)
                .superType(config.getBaseException())
                .filename(exceptionName)
                .build();

        // data
        String dataName = baseName + "Data";
        Directory data = new Directory.Builder("data", DirType.IMPL)
                .parent(srcBase)
                .superType(config.getBaseResultData())
                .filename(dataName)
                .parameterType(exception)
                .build();

        // messages
        String responseMessageName = baseName + "ResponseMessage";
        Directory responseMessage = new Directory.Builder(MESSAGE_DIR, DirType.IMPL)
                .parent(srcBase)
                .superType(config.getResponseBaseMessage())
                .parameterType(data)
                .parameterType(exception)
                .filename(responseMessageName)
                .build();
        String requestMessageName = baseName + "Message";
        Directory requestMessage = new Directory.Builder(MESSAGE_DIR, DirType.IMPL)
                .parent(srcBase)
                .superType(config.getRequestBaseMessage())
                .parameterType(service)
                .parameterType(responseMessage)
                .parameterType(data)
                .parameterType(exception)
                .filename(requestMessageName)
                .build();

        // server
        String serverName = baseName + "Server";
        Directory server = new Directory.Builder("server", DirType.IMPL)
                .parent(srcBase)
                .superType(config.getSuperServer())
                .filename(serverName)
                .metadata(Service.class)
                .build();

        // network components
        String networkComponentName = baseName + "NetComponent";
        Directory serviceNetworkComponent = new Directory.Builder("net", DirType.IMPL)
                .parent(srcBase)
                .superType(config.getSuperNetComponent())
                .interfaceType()
                .parameterType(requestMessage)
                .parameterType(responseMessage)
                .parameterType(data)
                .parameterType(exception)
                .filename(networkComponentName)
                .build();
        String netAdapterName = baseName + "NetAdapter";
        Directory serviceNetAdapter = new Directory.Builder("net", DirType.IMPL)
                .parent(srcBase)
                .superType(serviceNetworkComponent)
                .superType(config.getBaseNetComponent())
                .parameterType(requestMessage)
                .parameterType(responseMessage)
                .parameterType(data)
                .parameterType(exception)
                .metadata(Service.class)
                .filename(netAdapterName)
                .build();
        String networkReceiverName = baseName + "NetReceiver";
        Directory networkReceiver = new Directory.Builder("net", DirType.IMPL)
                .parent(srcBase)
                .superType(config.getSuperNetReceiver())
                .interfaceType()
                .parameterType(requestMessage)
                .filename(networkReceiverName)
                .build();
        String networkResponseReceiverName = baseName + "NetResponseReceiver";
        Directory networkResponseReceiver = new Directory.Builder("net", DirType.IMPL)
                .parent(srcBase)
                .superType(config.getSuperNetReceiver())
                .interfaceType()
                .parameterType(responseMessage)
                .filename(networkResponseReceiverName)
                .build();

        // create configuration
        BeanMapping serverBeanMapping = new BeanMapping.Builder(uncapitalize(serverName))
                .typeDir(server)
                .toTypeDir(server)
                .codeTemplate(configGenerator::simpleReturn)
                .build();
        BeanMapping adapterBeanMapping = new BeanMapping.Builder(uncapitalize(networkComponentName))
                .typeDir(serviceNetworkComponent)
                .toTypeDir(serviceNetAdapter)
                .codeTemplate(configGenerator::simpleReturn)
                .build();
        configuration.add(serverBeanMapping);
        configuration.add(adapterBeanMapping);
    }

    @Override
    public List<BeanMapping> getConfiguration() {
        return configuration;
    }
}
