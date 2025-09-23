package com.example.interview.week1.mapper;

import org.mapstruct.Mapper;

import com.example.interview.week1.dto.OrderDTO;
import com.example.interview.week1.model.Order;

@Mapper
public interface OrderMapper {

    OrderDTO toOrderDTO(Order order);

    Order toOrder(OrderDTO orderDTO);
}
