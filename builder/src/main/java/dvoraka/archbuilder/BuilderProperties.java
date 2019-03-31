package dvoraka.archbuilder;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "builder")
public class BuilderProperties {

    private String rootDirName = "rootDir";
    private String packageName = "package";

    private String appString = "App";
    private String configString = "Config";
    private String controllerString = "Controller";
    private String defaultString = "Default";
    private String exceptionString = "Exception";
    private String serverString = "Server";

    private Service service = new Service();


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

    public String getAppString() {
        return appString;
    }

    public void setAppString(String appString) {
        this.appString = appString;
    }

    public String getConfigString() {
        return configString;
    }

    public void setConfigString(String configString) {
        this.configString = configString;
    }

    public String getControllerString() {
        return controllerString;
    }

    public void setControllerString(String controllerString) {
        this.controllerString = controllerString;
    }

    public String getDefaultString() {
        return defaultString;
    }

    public void setDefaultString(String defaultString) {
        this.defaultString = defaultString;
    }

    public String getExceptionString() {
        return exceptionString;
    }

    public void setExceptionString(String exceptionString) {
        this.exceptionString = exceptionString;
    }

    public String getServerString() {
        return serverString;
    }

    public void setServerString(String serverString) {
        this.serverString = serverString;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public static class Service {

        private String name = "";
        private String string = "Service";


        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getString() {
            return string;
        }

        public void setString(String string) {
            this.string = string;
        }
    }
}
