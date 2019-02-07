package dvoraka.archbuilder.prototype.message.v1;

import dvoraka.archbuilder.prototype.data.PBudgetMessage;
import dvoraka.archbuilder.prototype.data.PData;

public class MessageApp {

    public static void main(String[] args) {
        System.out.println("Message app");

        PData data = new PData();

        PBudgetMessage message = new PBudgetMessage();
        message.setId("id");
        message.setData(data);
    }
}
