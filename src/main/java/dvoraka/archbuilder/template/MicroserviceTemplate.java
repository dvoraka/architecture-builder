package dvoraka.archbuilder.template;

import dvoraka.archbuilder.DirType;
import dvoraka.archbuilder.Directory;
import dvoraka.archbuilder.generate.JavaUtils;

public class MicroserviceTemplate {

    private Directory root;
    private Directory srcRoot;
    private Directory srcBase;
    private Directory srcBaseAbs;


    public MicroserviceTemplate(
            String rootDirName,
            String packageName // example.something with later substitution
    ) {
        root = new Directory.DirectoryBuilder(rootDirName)
                .type(DirType.ROOT)
                .parent(null)
                .build();

        srcRoot = new Directory.DirectoryBuilder("src/main/java") // replace
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

        Directory abstractService = new Directory.DirectoryBuilder("service")
                .type(DirType.SERVICE_ABSTRACT)
                .parent(srcBaseAbs)
                .typeName("java.util.Map")
                .build();

        Directory service = new Directory.DirectoryBuilder("service")
                .type(DirType.SERVICE)
                .parent(srcBase)
                .superType(abstractService)
                .filename("CoolMapService")
                .parameterType("java.lang.String")
                .parameterType("java.lang.Long")
                .build();

        Directory serviceImpl = new Directory.DirectoryBuilder("service")
                .type(DirType.SERVICE_IMPL)
                .parent(srcBase)
                .superType(service)
                .build();
    }
}
