package dvoraka.archbuilder.prototype.data.net;

import dvoraka.archbuilder.prototype.data.PBalanceData;
import dvoraka.archbuilder.prototype.data.PBalanceException;
import dvoraka.archbuilder.prototype.data.message.PBalanceMessage;
import dvoraka.archbuilder.prototype.data.message.PBalanceResponseMessage;
import dvoraka.archbuilder.sample.microservice.net.ServiceNetComponent;

/**
 * Balance network component interface.
 */
public interface PBalanceNetComponent extends ServiceNetComponent<
        PBalanceMessage,
        PBalanceResponseMessage,
        PBalanceData,
        PBalanceException> {
}
