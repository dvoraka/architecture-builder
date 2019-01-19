package dvoraka.archbuilder.sample.generic;

import java.io.Serializable;
import java.util.List;

public interface Interface2pb2am<A extends Number, B extends Serializable> {

    A first(List<A> data);

    B second(B data);
}
