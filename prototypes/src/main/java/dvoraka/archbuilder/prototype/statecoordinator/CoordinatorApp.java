package dvoraka.archbuilder.prototype.statecoordinator;

import dvoraka.archbuilder.prototype.statecoordinator.order.OrderData;
import dvoraka.archbuilder.prototype.statecoordinator.order.OrderStateCoordinator;

import java.util.concurrent.TimeUnit;

public class CoordinatorApp {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Coordinator app");

        OrderData order = new OrderData();
        order.setId(1);
        order.setUserId(2);
        order.setItemId(3);

        OrderStateCoordinator coordinator = new OrderStateCoordinator();
        coordinator.process(order);

        // wait for async stuff
        TimeUnit.SECONDS.sleep(5);
    }
}
