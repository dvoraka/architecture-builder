package dvoraka.archbuilder.prototype.actioncoordinator;

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
     * @throws RuntimeException when data are not found
     */
    //TODO: exception type
    void cancel(ID dataId);

    /**
     * Returns action count.
     *
     * @return the size/count
     */
    long getSize();
}
