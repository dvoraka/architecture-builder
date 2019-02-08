package dvoraka.archbuilder.prototype.message.v1;

import dvoraka.archbuilder.prototype.data.PData;
import dvoraka.archbuilder.prototype.data.message.PBudgetMessage;

public class MessageApp {

    public static void main(String[] args) {
        System.out.println("Message app v1");

        PData data = new PData();
        data.setData("DATA");

        PBudgetMessage message = new PBudgetMessage(data);
        message.setId("id");

        // get data
        PData data2 = message.getData();

        message.customMethod();
    }
}
