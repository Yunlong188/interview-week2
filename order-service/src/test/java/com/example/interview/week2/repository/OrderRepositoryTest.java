package com.example.interview.week2.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.example.interview.week2.model.Order;
import com.example.interview.week2.model.OrderItem;

@DataJpaTest
public class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @BeforeEach
    public void setUp() {
        orderRepository.deleteAll();
    }

    @Test
    public void testSave() {
        Order order = new Order();
        order.setUserId(UUID.randomUUID().toString());
        List<OrderItem> items = new ArrayList<>();
        OrderItem item1 = new OrderItem();
        item1.setProduct("product-1");
        item1.setQuantity(2);
        items.add(item1);
        OrderItem item2 = new OrderItem();
        item2.setProduct("product-2");
        item2.setQuantity(3);
        items.add(item2);
        order.setItems(items);
        orderRepository.save(order);
        Assertions.assertNotNull(order.getId());
    }

    @Test
    public void testFindById() throws Exception {
        Order order = new Order();
        order.setUserId(UUID.randomUUID().toString());
        List<OrderItem> items = new ArrayList<>();
        OrderItem item1 = new OrderItem();
        item1.setProduct("product-1");
        item1.setQuantity(2);
        items.add(item1);
        order.setItems(items);
        orderRepository.save(order);
        Order foundOrder = orderRepository.findById(order.getId()).orElse(null);
        Assertions.assertNotNull(foundOrder);
        Assertions.assertEquals(order.getId(), foundOrder.getId());
        Assertions.assertNotNull(foundOrder.getItems());
        Assertions.assertEquals(1, foundOrder.getItems().size());
        Assertions.assertEquals("product-1", foundOrder.getItems().get(0).getProduct());
        Assertions.assertEquals(2, foundOrder.getItems().get(0).getQuantity());
    }
}
