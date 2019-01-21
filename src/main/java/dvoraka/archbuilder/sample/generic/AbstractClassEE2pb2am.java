package dvoraka.archbuilder.sample.generic;

import java.util.List;

public abstract class AbstractClassEE2pb2am<T extends Number, U extends Number>
        extends AbstractClassE1pb2am<U> {

    public abstract U setData(List<T> data);
}
