package dvoraka.architecturebuilder;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

@Service
public class DefaultDirService implements DirService {

    @Override
    public void processDirs(Directory root) {
        if (root.getChildren().isEmpty()) {
            processLeaf(root);
        } else {
            for (Directory dir : root.getChildren()) {
                processDirs(dir);
            }
            processNode(root);
        }
    }

    private void processLeaf(Directory directory) {
        System.out.println("Processing leaf: " + directory.getName());
        if (!directory.getType().isAbstractType()) {
            try {
                Files.createDirectories(Paths.get(directory.getPath()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void processNode(Directory directory) {
        System.out.println("Processing node: " + directory.getName());
    }

    @Override
    public Optional<Directory> findByType(DirType type, Directory directory) {
        return Optional.empty();
    }

    @Override
    public Optional<Directory> findByTypeFrom(DirType type, Directory fromDirectory) {
        return Optional.empty();
    }

    @Override
    public Directory getRoot(Directory directory) {
        return null;
    }
}
