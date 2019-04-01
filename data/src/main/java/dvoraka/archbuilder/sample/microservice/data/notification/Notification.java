package dvoraka.archbuilder.sample.microservice.data.notification;

import dvoraka.archbuilder.sample.microservice.data.message.Message;

public interface Notification extends Message {

    NotificationType getType();

    NotificationData getData();
}
