package dvoraka.archbuilder;

public class BuilderHelper {

    private final BuilderProperties properties;

    private String rootDirName;
    private String packageName;
    private String serviceName;


    public BuilderHelper(BuilderProperties properties) {
        this.properties = properties;

        rootDirName = properties.getRootDirName();
        packageName = properties.getPackageName();
        serviceName = properties.getService().getName();
    }

    public String getRootDirName() {
        return rootDirName;
    }

    public void setRootDirName(String rootDirName) {
        this.rootDirName = rootDirName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String serviceFullName() {
        return serviceName + properties.getService().getString();
    }
}
