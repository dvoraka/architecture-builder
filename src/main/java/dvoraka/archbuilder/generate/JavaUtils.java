package dvoraka.archbuilder.generate;

import java.io.File;

public final class JavaUtils {

    private JavaUtils() {
    }

    /**
     * Returns a system independent relative filesystem path string for a package.
     *
     * @param packageName the package
     * @return the filesystem path string
     */
    public static String pkg2path(String packageName) {
        return packageName.replace('.', File.separatorChar);
    }

    public static String path2pkg(String path) {
        return path.replace(File.separatorChar, '.');
    }
}
