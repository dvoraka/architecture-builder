package dvoraka.archbuilder.prototype.data.net;

import dvoraka.archbuilder.prototype.data.PBalanceData;
import dvoraka.archbuilder.prototype.data.PBalanceException;
import dvoraka.archbuilder.prototype.data.message.PBalanceMessage;
import dvoraka.archbuilder.prototype.data.message.PBalanceResponseMessage;
import dvoraka.archbuilder.sample.microservice.net.Acknowledgment;
import dvoraka.archbuilder.sample.microservice.net.NetMessageListener;
import dvoraka.archbuilder.sample.microservice.net.ServiceNetComponent;

public class PBalanceNetComponent implements ServiceNetComponent<
        PBalanceMessage,
        PBalanceResponseMessage,
        PBalanceData,
        PBalanceException> {

    @Override
    public void addMessageListener(NetMessageListener<PBalanceMessage> listener) {

    }

    @Override
    public void removeMessageListener(NetMessageListener<PBalanceMessage> listener) {

    }

    @Override
    public void onMessage(PBalanceMessage message, Acknowledgment acknowledgment) {

    }

    @Override
    public void send(PBalanceResponseMessage message) throws Exception {

    }
}
