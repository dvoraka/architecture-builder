package dvoraka.archbuilder.template.arch;

import dvoraka.archbuilder.data.DirType;
import dvoraka.archbuilder.data.Directory;
import org.springframework.stereotype.Service;

import static dvoraka.archbuilder.template.arch.MicroserviceTemplate.MESSAGE_DIR;

public class NetSubmodule implements Submodule {

    private final String baseName;
    private final Directory service;
    private final NetTemplateConfig config;


    public NetSubmodule(String baseName, Directory service, NetTemplateConfig config) {
        this.baseName = baseName;
        this.service = service;
        this.config = config;
    }

    @Override
    public void addSubmodule(Directory srcBase) {

        // exception
        String exceptionName = getBaseName() + "Exception";
        Directory exception = new Directory.Builder("exception", DirType.IMPL)
                .parent(srcBase)
                .superTypeClass(config.getBaseException())
                .filename(exceptionName)
                .build();

        // data
        String dataName = getBaseName() + "Data";
        Directory data = new Directory.Builder("data", DirType.IMPL)
                .parent(srcBase)
                .superTypeClass(getConfig().getBaseResultData())
                .filename(dataName)
                .parameterTypeDir(exception)
                .build();

        // messages
        String responseMessageName = getBaseName() + "ResponseMessage";
        Directory responseMessage = new Directory.Builder(MESSAGE_DIR, DirType.IMPL)
                .parent(srcBase)
                .superTypeClass(getConfig().getResponseBaseMessage())
                .parameterTypeDir(data)
                .parameterTypeDir(exception)
                .filename(responseMessageName)
                .build();
        String requestMessageName = getBaseName() + "Message";
        Directory requestMessage = new Directory.Builder(MESSAGE_DIR, DirType.IMPL)
                .parent(srcBase)
                .superTypeClass(getConfig().getRequestBaseMessage())
                .parameterTypeDir(service)
                .parameterTypeDir(responseMessage)
                .parameterTypeDir(data)
                .parameterTypeDir(exception)
                .filename(requestMessageName)
                .build();

        // server
        String serverName = getBaseName() + "Server";
        Directory server = new Directory.Builder("server", DirType.IMPL)
                .parent(srcBase)
                .superTypeClass(config.getSuperServer())
                .filename(serverName)
                .metadataClass(Service.class)
                .build();

        // network components
        String networkComponentName = getBaseName() + "NetComponent";
        Directory serviceNetworkComponent = new Directory.Builder("net", DirType.IMPL)
                .parent(srcBase)
                .superTypeClass(getConfig().getSuperNetComponent())
                .interfaceType()
                .parameterTypeDir(requestMessage)
                .parameterTypeDir(responseMessage)
                .parameterTypeDir(data)
                .parameterTypeDir(exception)
                .filename(networkComponentName)
                .build();
        String netAdapterName = getBaseName() + "NetAdapter";
        Directory serviceNetAdapter = new Directory.Builder("net", DirType.IMPL)
                .parent(srcBase)
                .superType(serviceNetworkComponent)
                .superTypeClass(getConfig().getBaseNetComponent())
                .parameterTypeDir(requestMessage)
                .parameterTypeDir(responseMessage)
                .parameterTypeDir(data)
                .parameterTypeDir(exception)
                .metadataClass(Service.class)
                .filename(netAdapterName)
                .build();
        String networkReceiverName = getBaseName() + "NetReceiver";
        Directory networkReceiver = new Directory.Builder("net", DirType.IMPL)
                .parent(srcBase)
                .superTypeClass(getConfig().getSuperNetReceiver())
                .interfaceType()
                .parameterTypeDir(requestMessage)
                .filename(networkReceiverName)
                .build();
        String networkResponseReceiverName = getBaseName() + "NetResponseReceiver";
        Directory networkResponseReceiver = new Directory.Builder("net", DirType.IMPL)
                .parent(srcBase)
                .superTypeClass(getConfig().getSuperNetReceiver())
                .interfaceType()
                .parameterTypeDir(responseMessage)
                .filename(networkResponseReceiverName)
                .build();
    }

    public String getBaseName() {
        return baseName;
    }

    public NetTemplateConfig getConfig() {
        return config;
    }
}
