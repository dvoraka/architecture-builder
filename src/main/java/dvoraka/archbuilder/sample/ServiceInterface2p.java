package dvoraka.archbuilder.sample;

import dvoraka.archbuilder.sample.generic.Interface1p1am;

public interface ServiceInterface2p<A, B> extends Interface1p1am<B> {

    void process(A data);
}
