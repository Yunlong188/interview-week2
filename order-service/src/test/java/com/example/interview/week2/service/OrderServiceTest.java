package com.example.interview.week2.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.example.interview.week2.model.Order;
import com.example.interview.week2.model.OrderItem;
import com.example.interview.week2.repository.OrderRepository;
import com.example.interview.week2.service.impl.OrderServiceImpl;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Order sampleOrder;

    @BeforeEach
    void setUp() {
        sampleOrder = new Order();
        try {
            sampleOrder.setId("10");
            List<OrderItem> items = new ArrayList<>();
            OrderItem item = new OrderItem();
            item.setProduct("Widget");
            item.setQuantity(5);
            items.add(item);
            sampleOrder.setItems(items);
        } catch (Exception e) {
            // ignore if setters not present
        }
    }

    @Test
    void findById_whenOrderExists_returnsOrder() {
        when(orderRepository.findById("10")).thenReturn(Optional.of(sampleOrder));

        Order result = orderService.findById("10");

        assertNotNull(result);
        assertEquals(sampleOrder, result);
        verify(orderRepository, times(1)).findById("10");
    }

    @Test
    void findById_whenOrderNotFound_returnsNull() {
        when(orderRepository.findById("20")).thenReturn(Optional.empty());

        Order result = orderService.findById("20");

        assertNull(result);
        verify(orderRepository, times(1)).findById("20");
    }

    @Test
    void create_callsSaveAndReturnsId() {
        Order toSave = new Order();
        toSave.setUserId(UUID.randomUUID().toString());
        List<OrderItem> items = new ArrayList<>();
        OrderItem item = new OrderItem();
        item.setProduct("Widget");
        item.setQuantity(5);
        items.add(item);
        toSave.setItems(items);

        when(restTemplate.getForEntity(anyString(), eq(String.class)))
                .thenReturn(ResponseEntity.ok("{\"id\":\"" + toSave.getUserId() + "\",\"username\":\"testuser\"}"));
        when(orderRepository.save(toSave)).thenReturn(sampleOrder);

        String id = orderService.create(toSave);

        assertEquals("10", id);
        verify(orderRepository, times(1)).save(toSave);
    }

}
