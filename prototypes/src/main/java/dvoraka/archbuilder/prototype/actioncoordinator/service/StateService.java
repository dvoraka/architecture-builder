package dvoraka.archbuilder.prototype.actioncoordinator.service;

import dvoraka.archbuilder.sample.microservice.service.BaseService;

public interface StateService<D, ID> extends BaseService {

    ID process(D data, String transactionId);
}
