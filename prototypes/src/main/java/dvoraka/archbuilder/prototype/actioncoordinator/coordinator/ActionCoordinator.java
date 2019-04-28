package dvoraka.archbuilder.prototype.actioncoordinator.coordinator;

import dvoraka.archbuilder.sample.microservice.data.BaseException;

public interface ActionCoordinator<ID, D, E extends BaseException> {

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
     * @throws E when cancel fails
     */
    void cancel(ID dataId) throws E;

    /**
     * Returns action count.
     *
     * @return the size/count
     */
    long getSize();
}
