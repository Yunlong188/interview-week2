package com.example.interview.week2.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.interview.week2.model.Order;
import com.example.interview.week2.repository.OrderRepository;
import com.example.interview.week2.service.OrderService;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public Order findById(String id) {
        return orderRepository.findById(id).orElse(null);
    }

    @Override
    public String create(Order order) {
        return orderRepository.save(order).getId();
    }
}
