package dvoraka.archbuilder.sample.microservice.net;

import dvoraka.archbuilder.sample.microservice.data.notification.Notification;

public interface NotificationListener {

    void onNotification(Notification notification);
}
