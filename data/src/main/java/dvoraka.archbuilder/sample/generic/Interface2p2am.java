package dvoraka.archbuilder.sample.generic;

import java.util.Collection;
import java.util.List;

public interface Interface2p2am<A, B> {

    A first(List<A> data);

    Collection<B> second(B data);
}
