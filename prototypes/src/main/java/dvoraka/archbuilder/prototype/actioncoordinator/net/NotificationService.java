package dvoraka.archbuilder.prototype.actioncoordinator.net;

import dvoraka.archbuilder.sample.microservice.data.notification.Notification;
import dvoraka.archbuilder.sample.microservice.net.NetMessageListener;

public interface NotificationService {

    void publish(Notification notification);

    void subscribe(NetMessageListener<Notification> listener);
}
