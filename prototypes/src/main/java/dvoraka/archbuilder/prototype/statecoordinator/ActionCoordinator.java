package dvoraka.archbuilder.prototype.statecoordinator;

public interface ActionCoordinator<ID, D> {

    /**
     * Processes a data.
     *
     * @param data the data
     */
    void process(D data);

    /**
     * Cancels processing.
     *
     * @param dataId the data ID
     * @throws Exception
     */
    void cancel(ID dataId) throws Exception;
}
