package dvoraka.archbuilder.prototype.message.v2;

import dvoraka.archbuilder.prototype.data.PData;
import dvoraka.archbuilder.prototype.data.PMessage;

public class MessageApp {

    public static void main(String[] args) {
        System.out.println("Message app v2");

        PData data = new PData();
        data.setData("DATA");

        PMessage<PData> message = new PMessage<>(data);
        message.setId("id");

        // get data
        PData data2 = message.getData();

        // not so easy to add custom method but it could be on data
    }
}
