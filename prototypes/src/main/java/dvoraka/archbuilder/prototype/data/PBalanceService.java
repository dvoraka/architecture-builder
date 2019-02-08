package dvoraka.archbuilder.prototype.data;

import dvoraka.archbuilder.sample.microservice.service.BaseService;

public class PBalanceService implements BaseService {

    @Override
    public boolean isRunning() {
        return false;
    }
}
