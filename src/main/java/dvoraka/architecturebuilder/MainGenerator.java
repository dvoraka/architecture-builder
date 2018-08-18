package dvoraka.architecturebuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

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

        // generate code
        dirService.processDirs(directory, langGenerator::generate);
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
}
