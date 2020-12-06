package com.example.order.controller.grpc;
import com.example.order.*;
import com.example.order.dto.Customer;
import com.example.order.dto.ItemDTO;
import com.example.order.entities.OrderItem;
import com.example.order.service.OrderService;
import com.example.order.entities.Order;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@GrpcService
public class OrderServiceImpl extends orderServiceGrpc.orderServiceImplBase{
    private final OrderService orderService;

    @Autowired
    public OrderServiceImpl(OrderService orderService) {
        this.orderService = orderService;
    }

    @Override
    public void getOrder(GetRequestOrder request, StreamObserver<GetResponseOrder> responseStreamObserver) {
        List<Order> orders = orderService.getAllOrders();

        List<ProtoOrder> protoOrders = new ArrayList<>();
        for (Order order: orders) {
            ProtoOrder protoOrder = ProtoOrder.newBuilder()
                    .setId(order.getId().toString())
                    .setCustomerId(order.getCustomerId().toString())
                    .build();
            protoOrders.add(protoOrder);
        }
        GetResponseOrder response = GetResponseOrder.newBuilder().addAllOrder(protoOrders).build();
        responseStreamObserver.onNext(response);
        responseStreamObserver.onCompleted();
    }

    @Override
    public void getOrderedThing(GetRequestOrderedItem request, StreamObserver<GetResponseOrderedItem> responseStreamObserver) {
        List<OrderItem> orderedItems = orderService.getAllOrderItems();

        List<ProtoOrderedItem> protoOrderedThings = new ArrayList<>();
        for (OrderItem orderedItem: orderedItems) {
            ProtoOrderedItem protoOrderedThing = ProtoOrderedItem.newBuilder()
                    .setItemId(orderedItem.getItemId().toString())
                    .setOrderId(orderedItem.getOrderId().toString())
                    .build();
            protoOrderedThings.add(protoOrderedThing);
        }
        GetResponseOrderedItem response = GetResponseOrderedItem.newBuilder().addAllOrderedItems(protoOrderedThings).build();
        responseStreamObserver.onNext(response);
        responseStreamObserver.onCompleted();
    }

    @Override
    public void create(CreateRequest request, StreamObserver<CreateResponse> responseStreamObserver){
        ProtoCustomer protoCustomer = request.getCustomer();
        Customer customer = new Customer(UUID.randomUUID(), protoCustomer.getFirstName(), protoCustomer.getLastName());

        List<ProtoItem> protoItems = request.getItemsList();
        List<ItemDTO> items = new ArrayList<>();
        for (ProtoItem protoItem: protoItems) {
            ItemDTO item = new ItemDTO(UUID.randomUUID(), protoItem.getName(), protoItem.getPrice(), protoItem.getQuantity());
            items.add(item);
        }
        orderService.addOrder(items, customer);

        CreateResponse response = CreateResponse.newBuilder()
                .build();
        responseStreamObserver.onNext(response);
        responseStreamObserver.onCompleted();
    }
}