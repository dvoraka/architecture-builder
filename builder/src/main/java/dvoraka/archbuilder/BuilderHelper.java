package dvoraka.archbuilder;

public class BuilderHelper {

    private final BuilderProperties properties;


    public BuilderHelper(BuilderProperties properties) {
        this.properties = properties;
    }

    public String buildServiceName(String serviceName) {
        return serviceName + "Service";
    }
}
