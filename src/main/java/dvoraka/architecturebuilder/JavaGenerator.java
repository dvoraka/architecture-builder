package dvoraka.architecturebuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;

@Service
public class JavaGenerator implements Generator {

    private final DirService dirService;

    private final Logger log = LoggerFactory.getLogger(JavaGenerator.class);

    private final EnumMap<DirType, Consumer<Directory>> conf;


    @Autowired
    public JavaGenerator(DirService dirService) {
        this.dirService = requireNonNull(dirService);

        conf = new EnumMap<>(DirType.class);

        checkImplementation();
    }

    private void checkImplementation() {
        for (DirType type : DirType.values()) {
            if (!conf.containsKey(type)) {
                log.debug("Type not implemented: {}", type.toString());
            }
        }
    }

    @Override
    public void generate(Directory directory) {

    }
}
