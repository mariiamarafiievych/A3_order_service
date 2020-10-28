package order.entities;

import javax.persistence.*;
import java.util.UUID;

@Entity
public class OrderItem {
    @Id
    @GeneratedValue
    private UUID id;
    private UUID itemId;
    private UUID orderId;

    public OrderItem(){}

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getItemId() {
        return itemId;
    }

    public void setItemId(UUID thingId) {
        this.itemId = thingId;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }
}
