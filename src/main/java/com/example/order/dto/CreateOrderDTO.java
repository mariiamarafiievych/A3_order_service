package com.example.order.dto;

import java.util.List;

public class CreateOrderDTO {

    private Customer customerId;
    private List<ItemDTO> items;

    public Customer getCustomer() {
        return customerId;
    }

    public void setCustomer(Customer customerId) {
        this.customerId = customerId;
    }

    public List<ItemDTO> getItems() {
        return items;
    }

    public void setItems(List<ItemDTO> items) {
        this.items = items;
    }
}
