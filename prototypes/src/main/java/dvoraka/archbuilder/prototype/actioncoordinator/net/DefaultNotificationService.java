package dvoraka.archbuilder.prototype.actioncoordinator.net;

import dvoraka.archbuilder.sample.microservice.data.notification.Notification;
import dvoraka.archbuilder.sample.microservice.net.Acknowledgment;
import dvoraka.archbuilder.sample.microservice.net.NotificationListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

@Service
public class DefaultNotificationService implements NotificationService {

    private final NotificationComponent notificationComponent;

    private final List<NotificationListener> listeners;


    @Autowired
    public DefaultNotificationService(NotificationComponent notificationComponent) {
        this.notificationComponent = requireNonNull(notificationComponent);
        listeners = new ArrayList<>();
    }

    @PostConstruct
    public void start() {
        notificationComponent.addMessageListener(this::onNotification);
    }

    @Override
    public void publish(Notification notification) {
        try {
            notificationComponent.send(notification);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void subscribe(NotificationListener listener) {
        listeners.add(listener);
    }

    private void onNotification(Notification notification, Acknowledgment acknowledgment) {

        for (NotificationListener listener : listeners) {
            listener.onNotification(notification);
        }

        acknowledgment.ack();
    }
}
