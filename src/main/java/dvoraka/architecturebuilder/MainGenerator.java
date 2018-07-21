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

    private final Logger log = LoggerFactory.getLogger(MainGenerator.class);

    private final LangGenerator langGenerator;


    @Autowired
    public MainGenerator(DirService dirService) {
        this.dirService = requireNonNull(dirService);
        langGenerator = new JavaGenerator(dirService);
    }

    @Override
    public void generate(Directory directory) {

        // create dirs
        dirService.processDirLeafs(directory, this::createDirectory);

        // generate code
        dirService.processDirs(directory, langGenerator::generate);
    }

    private void createDirectory(Directory directory) {
        log.debug("Creating directory: {}", directory.getPath());
        if (!directory.getType().isAbstractType()) {
            try {
                Files.createDirectories(Paths.get(directory.getPath()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
