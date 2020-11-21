package order.service;

import javassist.NotFoundException;
import order.dto.Customer;
import order.dto.ItemDTO;
import order.entities.Order;
import order.entities.OrderItem;
import order.repo.OrderItemRepository;
import order.repo.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderService {

    private static final String SUPPLIER_URL = "http://supplier-service-new:8082";
    private final RestTemplate restTemplate = new RestTemplate();
    private final HttpHeaders headers = new HttpHeaders();
    private final HttpEntity<Object> headersEntity = new HttpEntity<>(headers);
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @Transactional
    public boolean addOrder(List<ItemDTO> requiredItems, Customer customer) {
        List<ItemDTO> itemsToOrder = new ArrayList<>();

        for (ItemDTO th : requiredItems) {
            ItemDTO item = getItemFromServiceByName(th.getName());
            int itemQuantity = item.getQuantity();

            if (itemQuantity > 0) {
                itemsToOrder.add(item);
                int newQuantity = itemQuantity - 1;
                th.setQuantity(newQuantity);
                saveNewQuantityForItem(th);
            } else {
                System.out.println("There is no " + th.getName() + ". It has been already sold.");
            }
        }
        return createOrder(itemsToOrder, customer);
    }

    private ItemDTO getItemFromServiceByName(String itemName) {
        ResponseEntity<ItemDTO> response = restTemplate
                .exchange(SUPPLIER_URL + "/items/name=" + itemName,
                        HttpMethod.GET, headersEntity, ItemDTO.class);
        return response.getBody();
    }

    private void saveNewQuantityForItem(ItemDTO itemWithNewQuantity) {
        HttpEntity<ItemDTO> item = new HttpEntity<>(itemWithNewQuantity);
        ResponseEntity<Void> response = restTemplate
                .exchange(SUPPLIER_URL + "/items/newQuantity",
                        HttpMethod.PUT, item, Void.class);
    }

    private boolean createOrder(List<ItemDTO> itemsToOrder, Customer customer) {
        boolean flag = false;
        UUID id = UUID.randomUUID();
        //double price = thingsToOrder.stream().mapToDouble(ThingDTO::getPrice).sum();
        Order order = new Order(id, customer.getCustomerId());
        for (ItemDTO th : itemsToOrder) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setItemId(th.getItemId());
            System.out.println("New order has created: " + order);
            orderRepository.save(order);
            orderItemRepository.save(orderItem);
            flag = true;
        }
        return flag;
    }

    @Transactional(readOnly = true)
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<OrderItem> getAllOrderItems(){
        return orderItemRepository.findAll();
    }

    @Transactional
    public Order getOrderById(UUID id) throws NotFoundException {
        Optional<Order> tempOrder = orderRepository.findById(id);
        if (tempOrder.isPresent())
            return tempOrder.get();
        else
            throw new NotFoundException(String.format("Order with id %s was not found", id));
    }
}
