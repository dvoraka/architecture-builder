package dvoraka.archbuilder.generate;

import dvoraka.archbuilder.Directory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

public final class JavaUtils {

    private static final Logger log = LoggerFactory.getLogger(JavaUtils.class);


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

    public static int compileSource(String pathString) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        log.info("Compiling source: {}...", pathString);

        // build a classpath string for the compiler
        URL[] urls = ((URLClassLoader) ClassLoader.getSystemClassLoader()).getURLs();
        StringBuilder cp = new StringBuilder();
        for (URL url : urls) {
            cp.append(url.getPath());
            cp.append(File.pathSeparator);
        }

        int exitCode = compiler.run(
                null,
                null,
                null,
                "-cp", cp.toString(),
                pathString
        );

        if (exitCode == 0) {
            log.info("Compilation OK");
        }

        return exitCode;
    }
}
