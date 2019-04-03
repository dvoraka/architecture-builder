package dvoraka.archbuilder.prototype.actioncoordinator.repository;

import dvoraka.archbuilder.prototype.actioncoordinator.model.OrderActionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderActionRepository extends JpaRepository<OrderActionStatus, Long> {
}
