package dvoraka.archbuilder;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "builder")
public class BuilderProperties {

    private String rootDirName = "rootDir";
    private String packageName = "package";

    private String appString = "App";
    private String configString = "Config";
    private String controllerString = "Controller";
    private String dataString = "Data";
    private String defaultString = "Default";
    private String exceptionString = "Exception";
    private String messageString = "Message";
    private String responseString = "Response";
    private String serverString = "Server";

    private String dataPkgName = "data";
    private String exceptionPkgName = "exception";
    private String serverPkgName = "server";

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

    public String getDataString() {
        return dataString;
    }

    public void setDataString(String dataString) {
        this.dataString = dataString;
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

    public String getMessageString() {
        return messageString;
    }

    public void setMessageString(String messageString) {
        this.messageString = messageString;
    }

    public String getServerString() {
        return serverString;
    }

    public String getResponseString() {
        return responseString;
    }

    public void setServerString(String serverString) {
        this.serverString = serverString;
    }

    public String getDataPkgName() {
        return dataPkgName;
    }

    public void setDataPkgName(String dataPkgName) {
        this.dataPkgName = dataPkgName;
    }

    public String getExceptionPkgName() {
        return exceptionPkgName;
    }

    public void setExceptionPkgName(String exceptionPkgName) {
        this.exceptionPkgName = exceptionPkgName;
    }

    public String getServerPkgName() {
        return serverPkgName;
    }

    public void setServerPkgName(String serverPkgName) {
        this.serverPkgName = serverPkgName;
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
        private String pkgName = "service";


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

        public String getPkgName() {
            return pkgName;
        }

        public void setPkgName(String pkgName) {
            this.pkgName = pkgName;
        }
    }
}
