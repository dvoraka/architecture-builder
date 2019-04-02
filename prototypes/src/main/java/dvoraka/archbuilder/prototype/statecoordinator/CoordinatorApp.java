package dvoraka.archbuilder.prototype.statecoordinator;

import dvoraka.archbuilder.prototype.statecoordinator.order.OrderActionCoordinator;
import dvoraka.archbuilder.prototype.statecoordinator.order.OrderData;
import dvoraka.archbuilder.prototype.statecoordinator.state.order.OrderStatus;

import java.util.concurrent.TimeUnit;

public class CoordinatorApp {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Coordinator app");

        OrderData order = new OrderData();
        order.setId(77);
        order.setUserId(2);
        order.setItemId(3);
        order.setStatus(OrderStatus.NEW);

        OrderData order2 = new OrderData();
        order2.setId(102);
        order2.setUserId(2);
        order2.setItemId(5);
        order2.setStatus(OrderStatus.NEW);

        OrderActionCoordinator coordinator = new OrderActionCoordinator();
        coordinator.start();

        coordinator.process(order);
        coordinator.process(order2);

        // wait for async stuff
        TimeUnit.SECONDS.sleep(30);
    }
}
