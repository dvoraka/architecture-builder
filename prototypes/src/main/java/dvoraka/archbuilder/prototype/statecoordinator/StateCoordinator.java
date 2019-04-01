package dvoraka.archbuilder.prototype.statecoordinator;

public interface StateCoordinator<ID, D> {

    void process(D data);

    void cancel(ID dataId) throws Exception;
}
