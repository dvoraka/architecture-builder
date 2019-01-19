package dvoraka.archbuilder.sample;

import dvoraka.archbuilder.sample.generic.Interface1pb1am;

import java.util.List;

public abstract class AbstractClassE1pb2am<T extends Number> implements Interface1pb1am<T> {

    public abstract void print(List<T> data);
}
