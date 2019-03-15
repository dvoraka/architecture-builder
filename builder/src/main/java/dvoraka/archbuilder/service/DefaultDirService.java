package dvoraka.archbuilder.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dvoraka.archbuilder.data.DirType;
import dvoraka.archbuilder.data.Directory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.function.Consumer;

@Service
public class DefaultDirService implements DirService {

    private ObjectMapper objectMapper;


    @Override
    public void processDirs(Directory root, Consumer<Directory> nodeProcessor, Consumer<Directory> leafProcessor) {
        if (root.getChildren().isEmpty()) {
            nodeProcessor.accept(root);
        } else {
            for (Directory dir : root.getChildren()) {
                processDirs(dir, nodeProcessor, leafProcessor);
            }
            nodeProcessor.accept(root);
        }
    }

    @Override
    public void processDirs(Directory root, Consumer<Directory> processor) {
        processDirs(root, processor, processor);
    }

    @Override
    public void processDirLeafs(Directory root, Consumer<Directory> processor) {
        if (root.getChildren().isEmpty()) {
            processor.accept(root);
        } else {
            for (Directory dir : root.getChildren()) {
                processDirLeafs(dir, processor);
            }
        }
    }

    @Override
    public void processDirNodes(Directory root, Consumer<Directory> processor) {
        if (!root.getChildren().isEmpty()) {
            for (Directory dir : root.getChildren()) {
                processDirNodes(dir, processor);
            }
            processor.accept(root);
        }
    }

    @Override
    public Optional<Directory> findByType(DirType type, Directory directory) {
        return findByTypeFrom(type, getRoot(directory));
    }

    @Override
    public Optional<Directory> findByTypeFrom(DirType type, Directory fromDirectory) {

        if (fromDirectory.getType() == type) {
            return Optional.of(fromDirectory);
        } else {
            for (Directory dir : fromDirectory.getChildren()) {
                Optional<Directory> found = findByTypeFrom(type, dir);
                if (found.isPresent()) {
                    return found;
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public Directory getRoot(Directory directory) {
        if (directory.isRoot()) {
            return directory;
        } else {
            return getRoot(directory.getParent());
        }
    }

    @Override
    public String toJson(Directory directory) throws JsonProcessingException {
        return objectMapper.writeValueAsString(directory);
    }

    @Override
    public Directory fromJson(String json) throws JsonProcessingException {
        try {
            return objectMapper.readValue(json, Directory.class);
        } catch (JsonProcessingException e) {
            throw e;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Path getFilePath(Directory directory) {
        return Paths.get(directory.getPath()
                + "/"
                + directory.getFilename().orElseThrow(RuntimeException::new)
        );
    }
}
