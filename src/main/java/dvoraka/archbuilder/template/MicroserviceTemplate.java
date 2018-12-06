package dvoraka.archbuilder.template;

import dvoraka.archbuilder.DirType;
import dvoraka.archbuilder.Directory;

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

        String pkgPath = "";
        srcBase = new Directory.DirectoryBuilder(pkgPath)
                .type(DirType.SRC_BASE)
                .parent(srcRoot)
                .build();

        String absPkgPath = "";
        srcBaseAbs = new Directory.DirectoryBuilder(absPkgPath)
                .type(DirType.SRC_BASE_ABSTRACT)
                .parent(root)
                .build();
    }
}
