package dvoraka.archbuilder.prototype.statecoordinator.order;

import dvoraka.archbuilder.prototype.statecoordinator.state.order.OrderStatus;

public class OrderData {

    private long id;
    private long userId;
    private long itemId;
    private OrderStatus status;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "OrderData{" +
                "id=" + id +
                ", userId=" + userId +
                ", itemId=" + itemId +
                ", status=" + status +
                '}';
    }
}
