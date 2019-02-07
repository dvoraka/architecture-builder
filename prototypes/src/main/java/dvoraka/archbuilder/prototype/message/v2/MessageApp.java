package dvoraka.archbuilder.prototype.message.v2;

import dvoraka.archbuilder.prototype.data.PData;
import dvoraka.archbuilder.prototype.data.PMessage;

public class MessageApp {

    public static void main(String[] args) {
        System.out.println("Message app v2");

        PData data = new PData();
        data.setData("DATA");

        PMessage<PData> message = new PMessage<>();
        message.setId("id");
        message.setData(data);
    }
}
