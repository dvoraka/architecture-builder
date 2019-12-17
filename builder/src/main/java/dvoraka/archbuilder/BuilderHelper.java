package dvoraka.archbuilder;

public class BuilderHelper {

    private final BuilderProperties properties;

    private final String rootDirName;
    private final String packageName;
    private final String baseName;


    public BuilderHelper(BuilderProperties properties) {
        this.properties = properties;

        rootDirName = properties.getRootDirName();
        packageName = properties.getPackageName();
        baseName = properties.getService().getName();
    }

    public BuilderHelper(
            BuilderProperties properties,
            String rootDirName,
            String packageName,
            String baseName
    ) {
        this.properties = properties;
        this.rootDirName = rootDirName;
        this.packageName = packageName;
        this.baseName = baseName;
    }

    public String getRootDirName() {
        return rootDirName;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getBaseName() {
        return baseName;
    }

    //////////////////////////////////////
    public String dataPkgName() {
        return properties.getDataPkgName();
    }

    public String exceptionPkgName() {
        return properties.getExceptionPkgName();
    }

    public String serverPkgName() {
        return properties.getServerPkgName();
    }

    //////////////////////////////////////
    public String configurationName() {
        return getBaseName() + properties.getConfigString();
    }

    public String dataName() {
        return getBaseName() + properties.getDataString();
    }

    public String dataServiceImplName() {
        return properties.getDefaultString() + dataServiceName();
    }

    public String dataServiceName() {
        return getBaseName() + properties.getDataString() + properties.getService().getString();
    }

    public String exceptionName() {
        return getBaseName() + properties.getExceptionString();
    }

    public String requestMessageName() {
        return getBaseName() + properties.getMessageString();
    }

    public String responseMessageName() {
        return getBaseName() + properties.getResponseString() + properties.getMessageString();
    }

    public String serverName() {
        return getBaseName() + properties.getServerString();
    }

    public String serviceAppName() {
        return getBaseName() + properties.getAppString();
    }

    public String serviceConfigName() {
        return getBaseName() + properties.getConfigString();
    }

    public String serviceControllerName() {
        return getBaseName() + properties.getControllerString();
    }

    public String serviceName() {
        return getBaseName() + properties.getService().getString();
    }

    public String restClientServiceName() {
        return "RestClient" + getBaseName() + properties.getService().getString();
    }

    public String restServerServiceName() {
        return "RestServer" + getBaseName() + properties.getService().getString();
    }

    public String serviceImplName() {
        return properties.getDefaultString() + serviceName();
    }

    public String restClientServiceImplName() {
        return properties.getDefaultString() + restClientServiceName();
    }

    public String restServerServiceImplName() {
        return properties.getDefaultString() + restServerServiceName();
    }

    public String servicePkgName() {
        return properties.getService().getPkgName();
    }
}
