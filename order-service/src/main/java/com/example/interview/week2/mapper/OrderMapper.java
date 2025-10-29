package com.example.interview.week2.mapper;

import org.mapstruct.Mapper;

import com.example.interview.week2.dto.OrderDTO;
import com.example.interview.week2.model.Order;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    OrderDTO toOrderDTO(Order order);

    Order toOrder(OrderDTO orderDTO);
}
