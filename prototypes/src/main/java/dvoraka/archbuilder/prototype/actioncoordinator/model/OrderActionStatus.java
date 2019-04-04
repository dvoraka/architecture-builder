package dvoraka.archbuilder.prototype.actioncoordinator.model;

import dvoraka.archbuilder.prototype.actioncoordinator.order.CreateOrderAction;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(
        name = "ORDER_ACTION_STATUS"
)
public class OrderActionStatus {

    @Id
    @GeneratedValue
    private long id;

    private long orderId;
    private String transactionId;

    private CreateOrderAction action;
    private CreateOrderAction previousAction;

    private boolean suspended;

    private String orderData;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public CreateOrderAction getAction() {
        return action;
    }

    public void setAction(CreateOrderAction action) {
        this.action = action;
    }

    public CreateOrderAction getPreviousAction() {
        return previousAction;
    }

    public void setPreviousAction(CreateOrderAction previousAction) {
        this.previousAction = previousAction;
    }

    public boolean isSuspended() {
        return suspended;
    }

    public void setSuspended(boolean suspended) {
        this.suspended = suspended;
    }

    public String getOrderData() {
        return orderData;
    }

    public void setOrderData(String orderData) {
        this.orderData = orderData;
    }
}
