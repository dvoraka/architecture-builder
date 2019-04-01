package dvoraka.archbuilder.prototype.statecoordinator;


import dvoraka.archbuilder.sample.microservice.data.message.Message;

public interface Notification extends Message {

    Object getData();
}
