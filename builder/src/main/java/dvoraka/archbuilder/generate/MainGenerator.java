package dvoraka.archbuilder.generate;

import dvoraka.archbuilder.data.DirType;
import dvoraka.archbuilder.data.Directory;
import dvoraka.archbuilder.exception.GeneratorException;
import dvoraka.archbuilder.service.DirService;
import dvoraka.archbuilder.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static java.util.Objects.requireNonNull;

@Service
public class MainGenerator implements Generator {

    private final DirService dirService;
    private final LangGenerator langGenerator;

    private final Logger log = LoggerFactory.getLogger(MainGenerator.class);

    private boolean removeClasses = true;


    @Autowired
    public MainGenerator(DirService dirService, LangGenerator langGenerator) {
        this.dirService = requireNonNull(dirService);
        this.langGenerator = requireNonNull(langGenerator);
    }

    @Override
    public void generate(Directory directory) {

        // create dirs
        dirService.processDirLeafs(directory, this::createDirectory);

        // start with SRC_ROOT
        dirService.findByType(DirType.SRC_ROOT, directory)
                .ifPresent(langGenerator::generate);

        // build the dependency data structure and then call generate in the right order
        Map<Directory, List<Directory>> dependencies = new HashMap<>();
        dirService.processDirs(directory, (dir) -> findDependencies(dir, dependencies));
        for (Directory dir : dependencies.keySet()) {
            generateDependencies(dependencies, dir);
        }

        // generate code (generate Spring configurations last)
        Collection<Directory> configurations = new HashSet<>();
        if (directory.getType() == DirType.SPRING_CONFIG) {
            configurations.add(directory);
        } else {
            dirService.processDirs(directory, langGenerator::generate);
        }
        configurations.forEach(config -> dirService.processDirs(config, langGenerator::generate));

        // remove class files
        if (removeClasses) {
            Directory rootDir = dirService.getRoot(directory);
            String rootDirName = rootDir.getName();
            try {
                Utils.removeClassFiles(rootDirName);
            } catch (IOException e) {
                throw new GeneratorException("Removing failed.", e);
            }
        }
    }

    private void generateDependencies(Map<Directory, List<Directory>> dependencies, Directory directory) {
        List<Directory> dirDeps = dependencies.get(directory);
        for (Directory dependency : dirDeps) {
            if (dependencies.containsKey(dependency)) {
                generateDependencies(dependencies, dependency);
            } else {
                langGenerator.generate(dependency);
            }
        }

        langGenerator.generate(directory);
    }

    private void createDirectory(Directory directory) {
        if (!directory.getType().isAbstract()) {
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

    public boolean isRemoveClasses() {
        return removeClasses;
    }

    public void setRemoveClasses(boolean removeClasses) {
        this.removeClasses = removeClasses;
    }

    @Override
    public String toString() {
        return "MainGenerator{" +
                "removeClasses=" + removeClasses +
                '}';
    }
}
