package com.example.interview.week1.service;

import com.example.interview.week1.model.Order;

public interface OrderService {
    
    Order findById(String id);

    String create(Order order);
}
