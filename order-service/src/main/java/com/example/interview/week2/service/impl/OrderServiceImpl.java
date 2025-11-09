package com.example.interview.week2.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.example.interview.week2.exception.BadRequestException;
import com.example.interview.week2.model.Order;
import com.example.interview.week2.repository.OrderRepository;
import com.example.interview.week2.service.OrderService;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private RestTemplate restTemplate;

    @Override
    public Order findById(String id) {
        return orderRepository.findById(id).orElse(null);
    }

    @Override
    public String create(Order order) {
        try {
            restTemplate.getForEntity("http://user-service:8082/api/users/{id}", String.class,
                    order.getUserId());
        } catch (HttpClientErrorException.NotFound e) {
            throw new BadRequestException("User with ID " + order.getUserId() + " does not exist.");
        } catch (HttpServerErrorException e) {
            // 服务端异常
        } catch (RestClientException e) {
            // 其他网络或客户异常
        }
        return orderRepository.save(order).getId();
    }
}
