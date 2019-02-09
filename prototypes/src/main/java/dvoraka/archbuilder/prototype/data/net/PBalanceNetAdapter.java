package dvoraka.archbuilder.prototype.data.net;

import dvoraka.archbuilder.prototype.data.PBalanceData;
import dvoraka.archbuilder.prototype.data.PBalanceException;
import dvoraka.archbuilder.prototype.data.message.PBalanceMessage;
import dvoraka.archbuilder.prototype.data.message.PBalanceResponseMessage;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

public class PBalanceNetAdapter
        extends PBaseNetComponent<PBalanceMessage, PBalanceResponseMessage, PBalanceData, PBalanceException>
        implements PBalanceNetComponent, MessageListener {

    private RabbitTemplate rabbitTemplate;


    @Override
    public void send(PBalanceResponseMessage message) throws Exception {
        rabbitTemplate.send(null);
    }

    @Override
    public void onMessage(Message message) {
        onMessage(null, null);
    }

    @RabbitListener
    public void receive() {

    }
}
