package dvoraka.architecturebuilder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static java.util.Objects.requireNonNull;

@Service
public class JavaGenerator implements Generator {

    private final DirService dirService;


    @Autowired
    public JavaGenerator(DirService dirService) {
        this.dirService = requireNonNull(dirService);
    }

    @Override
    public void generate(Directory directory) {

    }
}
