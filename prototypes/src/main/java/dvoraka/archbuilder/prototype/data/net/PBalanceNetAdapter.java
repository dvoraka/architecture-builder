package dvoraka.archbuilder.prototype.data.net;

import dvoraka.archbuilder.prototype.data.PBalanceData;
import dvoraka.archbuilder.prototype.data.PBalanceException;
import dvoraka.archbuilder.prototype.data.message.PBalanceMessage;
import dvoraka.archbuilder.prototype.data.message.PBalanceResponseMessage;

public class PBalanceNetAdapter
        extends PBaseNetComponent<PBalanceMessage, PBalanceResponseMessage, PBalanceData, PBalanceException>
        implements PBalanceNetComponent {

    @Override
    public void send(PBalanceResponseMessage message) throws Exception {
    }
}
