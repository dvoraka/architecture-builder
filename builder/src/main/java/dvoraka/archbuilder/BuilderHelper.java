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

    public String dataServiceImplName() {
        return properties.getDefaultString() + dataServiceName();
    }

    public String dataServiceName() {
        return getBaseName() + "Data" + properties.getService().getString();
    }

    public String exceptionName() {
        return getBaseName() + properties.getExceptionString();
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

    public String serviceImplName() {
        return properties.getDefaultString() + serviceName();
    }

    public String servicePkgName() {
        return properties.getService().getPkgName();
    }
}
