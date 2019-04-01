package dvoraka.archbuilder.sample.microservice.data.notification;

import dvoraka.archbuilder.sample.microservice.data.message.Message;

import java.util.Map;

public interface Notification extends Message {

    NotificationType getType();

    Map<String, Object> getData();
}
