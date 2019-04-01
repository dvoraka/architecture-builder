package dvoraka.archbuilder.prototype.statecoordinator;

import dvoraka.archbuilder.prototype.statecoordinator.order.OrderStateCoordinator;

public class CoordinatorApp {
    public static void main(String[] args) {
        System.out.println("Coordinator app");

        OrderStateCoordinator coordinator = new OrderStateCoordinator();
    }
}
