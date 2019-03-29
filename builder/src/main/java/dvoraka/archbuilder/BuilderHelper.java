package dvoraka.archbuilder;

public class BuilderHelper {

    private final BuilderProperties properties;

    private String rootDirName;
    private String packageName;
    private String serviceName;


    public BuilderHelper(BuilderProperties properties) {
        this.properties = properties;

        serviceName = properties.getService().getName();
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
}
