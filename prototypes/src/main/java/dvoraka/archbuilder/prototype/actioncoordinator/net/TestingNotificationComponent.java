package dvoraka.archbuilder.prototype.actioncoordinator.net;

import dvoraka.archbuilder.sample.microservice.data.notification.Notification;
import dvoraka.archbuilder.sample.microservice.net.receive.BaseNetReceiver;
import org.springframework.stereotype.Component;

@Component
public class TestingNotificationComponent
        extends BaseNetReceiver<Notification>
        implements NotificationComponent {

    @Override
    public void send(Notification notification) {
        log.debug("Sending: {}", notification);
    }
}
