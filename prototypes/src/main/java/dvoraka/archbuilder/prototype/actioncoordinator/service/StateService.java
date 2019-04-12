package dvoraka.archbuilder.prototype.actioncoordinator.service;

import dvoraka.archbuilder.sample.microservice.service.BaseService;

public interface StateService<D, ID> extends BaseService {

    /**
     * Processes a data. A transaction ID is for idempotency.
     *
     * @param data          the data to process
     * @param transactionId the call ID
     * @return the data ID
     */
    ID process(D data, String transactionId);

    /**
     * Cancels processing.
     *
     * @param id the data ID
     */
    void cancel(ID id);
}
