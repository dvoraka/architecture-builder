package dvoraka.archbuilder.prototype.actioncoordinator.net;

import dvoraka.archbuilder.sample.microservice.data.notification.Notification;
import dvoraka.archbuilder.sample.microservice.net.NetMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

@Service
public class DefaultNotificationService implements NotificationService {

    private final NotificationComponent notificationComponent;

    private final List<NetMessageListener<Notification>> listeners;


    @Autowired
    public DefaultNotificationService(NotificationComponent notificationComponent) {
        this.notificationComponent = requireNonNull(notificationComponent);
        listeners = new ArrayList<>();
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
    public void subscribe(NetMessageListener<Notification> listener) {
        listeners.add(listener);
    }
}
