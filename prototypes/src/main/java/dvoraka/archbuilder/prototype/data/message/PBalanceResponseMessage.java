package dvoraka.archbuilder.prototype.data.message;

import dvoraka.archbuilder.prototype.data.PBalanceData;
import dvoraka.archbuilder.prototype.data.PBalanceException;
import dvoraka.archbuilder.sample.microservice.data.message.ResponseMessage;

public class PBalanceResponseMessage extends ResponseMessage<PBalanceData, PBalanceException> {

    protected PBalanceResponseMessage(String corrId, PBalanceData resultData) {
        super(corrId, resultData);
    }
}
