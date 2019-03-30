package dvoraka.archbuilder;

public class BuilderHelper {

    private final BuilderProperties properties;

    private final String rootDirName;
    private final String packageName;
    private final String serviceName;


    public BuilderHelper(BuilderProperties properties) {
        this.properties = properties;

        rootDirName = properties.getRootDirName();
        packageName = properties.getPackageName();
        serviceName = properties.getService().getName();
    }

    public BuilderHelper(
            BuilderProperties properties,
            String rootDirName,
            String packageName,
            String serviceName
    ) {
        this.properties = properties;
        this.rootDirName = rootDirName;
        this.packageName = packageName;
        this.serviceName = serviceName;
    }

    public String getRootDirName() {
        return rootDirName;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String serviceFullName() {
        return getServiceName() + properties.getService().getString();
    }

    public String serviceImplName() {
        return properties.getDefaultString() + serviceFullName();
    }

    public String serviceConfigName() {
        return getServiceName() + properties.getConfigString();
    }

    public String serviceAppName() {
        return getServiceName() + properties.getAppString();
    }
}
