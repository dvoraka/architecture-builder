package dvoraka.architecturebuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.requireNonNull;

@Service
public class MainGenerator implements Generator {

    private final DirService dirService;
    private final LangGenerator langGenerator;

    private final Logger log = LoggerFactory.getLogger(MainGenerator.class);


    @Autowired
    public MainGenerator(DirService dirService, LangGenerator langGenerator) {
        this.dirService = requireNonNull(dirService);
        this.langGenerator = requireNonNull(langGenerator);
    }

    @Override
    public void generate(Directory directory) {

        // create dirs
        dirService.processDirLeafs(directory, this::createDirectory);

        // print nodes
        dirService.processDirNodes(directory, System.out::println);

        // build the dependency data structure and then call generate in the right order
        Map<Directory, List<Directory>> dependencies = new HashMap<>();
        dirService.processDirs(directory, (dir) -> findDependencies(dir, dependencies));
        generateDependencies(dependencies, null);

        System.exit(0);

        // generate code
        dirService.processDirs(directory, langGenerator::generate);
    }

    private void generateDependencies(Map<Directory, List<Directory>> dependencies, Directory directory) {

        if (directory == null) {
            directory = dependencies.keySet().iterator().next();
        }

        List<Directory> deps = dependencies.get(directory);

        for (Directory dep : deps) {

            if (dependencies.containsKey(dep)) {

                generateDependencies(dependencies, dep);

            } else {
                // generate
                System.out.println("Generate: " + dep.getType());
            }
        }

        // generate
        System.out.println("Generate: " + directory.getType());
    }

    private void createDirectory(Directory directory) {
        if (!directory.getType().isAbstractType()) {
            log.debug("Creating directory: {}", directory.getPath());
            try {
                Files.createDirectories(Paths.get(directory.getPath()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void findDependencies(Directory directory, Map<Directory, List<Directory>> dependencies) {
        if (!directory.getDependencies().isEmpty()) {
            dependencies.put(directory, directory.getDependencies());
        }
    }
}
