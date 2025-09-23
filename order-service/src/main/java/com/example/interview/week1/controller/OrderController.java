package com.example.interview.week1.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.interview.week1.dto.OrderDTO;
import com.example.interview.week1.mapper.OrderMapper;
import com.example.interview.week1.service.OrderService;
import com.example.interview.week1.utils.Preconditions;
import com.example.interview.week1.utils.RestPreconditions;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderMapper orderMapper;
    private final OrderService orderService;

    public OrderController(OrderMapper orderMapper, OrderService orderService) {
        this.orderMapper = orderMapper;
        this.orderService = orderService;
    }

    @GetMapping("/{id}")
    public OrderDTO findById(@PathVariable("id") String id) {
        return orderMapper.toOrderDTO(RestPreconditions.checkNotFound(orderService.findById(id)));
    }

    @PostMapping
    public String create(@RequestBody @Valid OrderDTO orderDTO) {
        Preconditions.checkNotNull(orderDTO);
        return orderService.create(orderMapper.toOrder(orderDTO));
    }
}
