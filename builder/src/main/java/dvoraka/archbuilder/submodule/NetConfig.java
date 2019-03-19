package dvoraka.archbuilder.submodule;

public class NetConfig {

    private final Class<?> baseResultData;
    private final Class<?> baseException;

    private final Class<?> requestBaseMessage;
    private final Class<?> responseBaseMessage;

    private final Class<?> superNetComponent;
    private final Class<?> superNetReceiver;
    private final Class<?> baseNetComponent;

    private final Class<?> superServer;


    public NetConfig(
            Class<?> baseResultData,
            Class<?> baseException,
            Class<?> requestBaseMessage,
            Class<?> responseBaseMessage,
            Class<?> superNetComponent,
            Class<?> superNetReceiver,
            Class<?> baseNetComponent,
            Class<?> superServer
    ) {
        this.baseResultData = baseResultData;
        this.baseException = baseException;
        this.requestBaseMessage = requestBaseMessage;
        this.responseBaseMessage = responseBaseMessage;
        this.superNetComponent = superNetComponent;
        this.superNetReceiver = superNetReceiver;
        this.baseNetComponent = baseNetComponent;
        this.superServer = superServer;
    }

    public Class<?> getBaseResultData() {
        return baseResultData;
    }

    public Class<?> getBaseException() {
        return baseException;
    }

    public Class<?> getRequestBaseMessage() {
        return requestBaseMessage;
    }

    public Class<?> getResponseBaseMessage() {
        return responseBaseMessage;
    }

    public Class<?> getSuperNetComponent() {
        return superNetComponent;
    }

    public Class<?> getSuperNetReceiver() {
        return superNetReceiver;
    }

    public Class<?> getBaseNetComponent() {
        return baseNetComponent;
    }

    public Class<?> getSuperServer() {
        return superServer;
    }
}
