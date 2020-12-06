package com.example.order.controller.rest;

import com.example.order.dto.Customer;
import com.example.order.dto.ItemDTO;
import com.example.order.service.OrderService;
import javassist.NotFoundException;
import com.example.order.dto.CreateOrderDTO;
import com.example.order.entities.Order;
import com.example.order.entities.OrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("orders")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<List<Order>> show(){
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping({"orderItems"})
    public ResponseEntity<List<OrderItem>> showItems(){
        return ResponseEntity.ok(orderService.getAllOrderItems());
    }

    @GetMapping({"id"})
    public ResponseEntity<Order> showById(@PathVariable UUID id) throws NotFoundException{
        return ResponseEntity.ok(orderService.getOrderById(id));
    }


    @PostMapping
    public ResponseEntity<Void> create(@RequestBody CreateOrderDTO createOrder){
        Customer customer = createOrder.getCustomer();
        System.out.println(customer.getCustomerId());
        List<ItemDTO> items = createOrder.getItems();
        if(orderService.addOrder(items, customer))
            return ResponseEntity.ok().build();
        else
            return ResponseEntity.badRequest().build();
    }


}
