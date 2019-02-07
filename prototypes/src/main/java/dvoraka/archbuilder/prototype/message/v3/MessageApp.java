package dvoraka.archbuilder.prototype.message.v3;

import dvoraka.archbuilder.prototype.data.PData;
import dvoraka.archbuilder.prototype.data.PInfoMessage;

public class MessageApp {

    public static void main(String[] args) {
        System.out.println("Message app v3");

        PData data = new PData();
        data.setData("DATA");

        PInfoMessage message = new PInfoMessage(data);
        message.setId("id");

        // get data
        PData data2 = message.getData();

        message.customMethod();
    }
}
