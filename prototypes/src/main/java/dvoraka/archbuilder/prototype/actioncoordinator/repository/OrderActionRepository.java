package dvoraka.archbuilder.prototype.actioncoordinator.repository;

import dvoraka.archbuilder.prototype.actioncoordinator.model.OrderActionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderActionRepository extends JpaRepository<OrderActionStatus, Long> {

    Optional<OrderActionStatus> findByOrderId(long orderId);
}
