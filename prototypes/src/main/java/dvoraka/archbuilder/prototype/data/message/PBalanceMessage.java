package dvoraka.archbuilder.prototype.data.message;

import dvoraka.archbuilder.prototype.data.PBalanceData;
import dvoraka.archbuilder.prototype.data.PBalanceException;
import dvoraka.archbuilder.prototype.data.PBalanceService;
import dvoraka.archbuilder.sample.microservice.data.message.RequestMessage;

public class PBalanceMessage extends RequestMessage<
        PBalanceService,
        PBalanceResponseMessage,
        PBalanceData,
        PBalanceException> {

    @Override
    public PBalanceResponseMessage apply(PBalanceService service) {
        return null;
    }
}
