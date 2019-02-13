package dvoraka.archbuilder.prototype.data.net;

import com.rabbitmq.client.Channel;
import dvoraka.archbuilder.prototype.data.PBalanceData;
import dvoraka.archbuilder.prototype.data.PBalanceException;
import dvoraka.archbuilder.prototype.data.message.PBalanceMessage;
import dvoraka.archbuilder.prototype.data.message.PBalanceResponseMessage;
import dvoraka.archbuilder.sample.microservice.net.BaseNetComponent;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class PBalanceRabbitMqNetAdapter
        extends BaseNetComponent<PBalanceMessage, PBalanceResponseMessage, PBalanceData, PBalanceException>
        implements PBalanceNetComponent {

    @Autowired
    private RabbitTemplate rabbitTemplate;


    @Override
    public void send(PBalanceResponseMessage message) throws Exception {
        rabbitTemplate.send(null);
    }

    @RabbitListener(queuesToDeclare = @Queue(name = "test"))
    public void receive(Message message, Channel channel) throws IOException {
        System.out.println("Receive: " + message);

//        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);

        // translate message

        onMessage(null, null);
    }
}
