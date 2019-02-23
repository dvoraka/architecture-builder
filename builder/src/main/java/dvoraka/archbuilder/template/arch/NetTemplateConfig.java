package dvoraka.archbuilder.template.arch;

public class NetTemplateConfig {

    private final Class<?> baseResultData;

    private final Class<?> requestBaseMessage;
    private final Class<?> responseBaseMessage;

    private final Class<?> superNetComponent;
    private final Class<?> superNetReceiver;
    private final Class<?> baseNetComponent;


    public NetTemplateConfig(
            Class<?> baseResultData,
            Class<?> requestBaseMessage,
            Class<?> responseBaseMessage,
            Class<?> superNetComponent,
            Class<?> superNetReceiver,
            Class<?> baseNetComponent
    ) {
        this.baseResultData = baseResultData;
        this.requestBaseMessage = requestBaseMessage;
        this.responseBaseMessage = responseBaseMessage;
        this.superNetComponent = superNetComponent;
        this.superNetReceiver = superNetReceiver;
        this.baseNetComponent = baseNetComponent;
    }

    public Class<?> getBaseResultData() {
        return baseResultData;
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
}
