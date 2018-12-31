package dvoraka.archbuilder.template;

import dvoraka.archbuilder.DirType;
import dvoraka.archbuilder.Directory;
import dvoraka.archbuilder.generate.JavaUtils;
import dvoraka.archbuilder.test.microservice.data.ResultData;
import dvoraka.archbuilder.test.microservice.data.message.ResponseMessage;

import java.util.List;

public class MicroserviceTemplate implements Template {

    public static final String JAVA_SRC_DIR = "src/main/java";

    private Directory root;
    private Directory srcRoot;
    private Directory srcBase;
    private Directory srcBaseAbs;


    public MicroserviceTemplate(
            String rootDirName,
            String packageName,
            Class<?> superService,
            List<Class<?>> typeArguments,
            String serviceName,
            Class<?> superServer,
            Class<?> requestSuperMessage
    ) {
        root = new Directory.DirectoryBuilder(rootDirName)
                .type(DirType.ROOT)
                .parent(null)
                .build();

        srcRoot = new Directory.DirectoryBuilder(JAVA_SRC_DIR)
                .type(DirType.SRC_ROOT)
                .parent(root)
                .build();

        String pkgPath = JavaUtils.pkg2path(packageName);
        srcBase = new Directory.DirectoryBuilder(pkgPath)
                .type(DirType.SRC_BASE)
                .parent(srcRoot)
                .build();

        String absPkgPath = "";
        srcBaseAbs = new Directory.DirectoryBuilder(absPkgPath)
                .type(DirType.SRC_BASE_ABSTRACT)
                .parent(root)
                .build();

        // service
        Directory abstractService = new Directory.DirectoryBuilder("service")
                .type(DirType.SERVICE_ABSTRACT)
                .parent(srcBaseAbs)
                .typeClass(superService)
                .build();

        Directory.DirectoryBuilder serviceBuilder = new Directory.DirectoryBuilder("service")
                .type(DirType.SERVICE)
                .parent(srcBase)
                .superType(abstractService)
                .filename(serviceName);
        for (Class<?> typeArgument : typeArguments) {
            serviceBuilder.parameterTypeClass(typeArgument);
        }
        Directory service = serviceBuilder
                .build();

        Directory serviceImpl = new Directory.DirectoryBuilder("service")
                .type(DirType.SERVICE_IMPL)
                .parent(srcBase)
                .superType(service)
                .build();

        // messages
        Directory requestMessageAbs = new Directory.DirectoryBuilder("")
                .type(DirType.ABSTRACT)
                .parent(srcBase)
                .typeClass(requestSuperMessage)
                .build();

        String requestMessageName = serviceName + "Message";
        Directory requestMessage = new Directory.DirectoryBuilder("data/message")
                .type(DirType.IMPL)
                .parent(srcBase)
                .superType(requestMessageAbs)
                .parameterTypeDir(service)
                .filename(requestMessageName)
                .build();

        Directory responseMessageAbs = new Directory.DirectoryBuilder("")
                .type(DirType.ABSTRACT)
                .parent(srcBase)
                .typeClass(ResponseMessage.class)
                .build();

        String responseMessageName = serviceName + "ResponseMessage";
        Directory responseMessage = new Directory.DirectoryBuilder("data/message")
                .type(DirType.IMPL)
                .parent(srcBase)
                .superType(responseMessageAbs)
                .parameterTypeClass(ResultData.class)
                .filename(responseMessageName)
                .build();

        // server
        Directory serverAbs = new Directory.DirectoryBuilder("")
                .type(DirType.ABSTRACT)
                .parent(srcBase)
                .typeClass(superServer)
                .build();

        String serverName = serviceName + "Server";
        Directory server = new Directory.DirectoryBuilder("server")
                .type(DirType.IMPL)
                .parent(srcBase)
                .superType(serverAbs)
                .filename(serverName)
                .build();

        // application properties
        Directory srcProps = new Directory.DirectoryBuilder("src/main/resources")
                .type(DirType.SRC_PROPERTIES)
                .parent(root)
                .filename("application.properties")
                .text("prop1=value\nprop2=value2\n")
                .build();
    }

    @Override
    public Directory getRootDirectory() {
        return root;
    }
}
