package com.example.interview.week2.service;

import com.example.interview.week2.model.Order;

public interface OrderService {
    
    Order findById(String id);

    String create(Order order);
}
