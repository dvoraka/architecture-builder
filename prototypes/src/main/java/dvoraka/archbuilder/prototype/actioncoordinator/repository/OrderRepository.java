package dvoraka.archbuilder.prototype.actioncoordinator.repository;

import dvoraka.archbuilder.prototype.actioncoordinator.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
