package order.dto;

import order.entities.OrderItem;

import java.util.List;

public class OrderItemsDTO {
    private List<OrderItem> orderItems;

    public OrderItemsDTO(){}

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }
}
