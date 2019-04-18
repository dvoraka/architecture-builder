package dvoraka.archbuilder.submodule.net;

import dvoraka.archbuilder.BuilderHelper;
import dvoraka.archbuilder.data.DirType;
import dvoraka.archbuilder.data.Directory;
import dvoraka.archbuilder.exception.GeneratorException;
import dvoraka.archbuilder.springconfig.BeanMapping;
import dvoraka.archbuilder.springconfig.SpringConfigGenerator;
import dvoraka.archbuilder.template.TemplateHelper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static dvoraka.archbuilder.util.Utils.uncapitalize;

public class ConfigurableNetSubmodule implements NetSubmodule, TemplateHelper {

    public static final String MESSAGE_DIR = "data/message";

    private final BuilderHelper helper;
    private final String baseName;
    private final Directory service;
    private final NetConfig config;

    private final SpringConfigGenerator configGenerator;

    private final List<BeanMapping> configuration;


    public ConfigurableNetSubmodule(
            BuilderHelper helper,
            Directory service,
            NetConfig config,
            SpringConfigGenerator configGenerator
    ) {
        this.helper = helper;
        this.baseName = helper.getBaseName();
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
        Directory exception = new Directory.Builder(helper.exceptionPkgName(), DirType.IMPL)
                .parent(srcBase)
                .superType(config.getBaseException())
                .filename(helper.exceptionName())
                .build();

        // data
        Directory data = new Directory.Builder(helper.dataPkgName(), DirType.IMPL)
                .parent(srcBase)
                .superType(config.getBaseResultData())
                .filename(helper.dataName())
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
        Directory requestMessage = new Directory.Builder(MESSAGE_DIR, DirType.IMPL)
                .parent(srcBase)
                .superType(config.getRequestBaseMessage())
                .parameterType(service)
                .parameterType(responseMessage)
                .parameterType(data)
                .parameterType(exception)
                .filename(helper.requestMessageName())
                .build();

        // server
        Directory server = new Directory.Builder(helper.serverPkgName(), DirType.IMPL)
                .parent(srcBase)
                .superType(config.getSuperServer())
                .filename(helper.serverName())
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
        BeanMapping serverBeanMapping = new BeanMapping.Builder(uncapitalize(helper.serverName()))
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
    public Collection<BeanMapping> getConfiguration() {
        return configuration;
    }
}
