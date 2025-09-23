package com.example.interview.week1.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.interview.week1.dto.OrderDTO;
import com.example.interview.week1.mapper.OrderMapper;
import com.example.interview.week1.model.Order;
import com.example.interview.week1.service.OrderService;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private OrderMapper orderMapper;

    @Test
    void findById_whenFound_returnsDto() throws Exception {
        Order order = new Order();
        order.setId("1");
        OrderDTO dto = new OrderDTO();
        dto.setId("1");

        when(orderService.findById("1")).thenReturn(order);
        when(orderMapper.toOrderDTO(order)).thenReturn(dto);

        mockMvc.perform(get("/orders/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));

        verify(orderService, times(1)).findById("1");
        verify(orderMapper, times(1)).toOrderDTO(order);
    }

    @Test
    void findById_whenNotFound_returns404() throws Exception {
        when(orderService.findById("9")).thenReturn(null);

        mockMvc.perform(get("/orders/9").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(orderService, times(1)).findById("9");
        verify(orderMapper, never()).toOrderDTO(any());
    }

    @Test
    void create_validDto_callsServiceAndReturnsId() throws Exception {
        OrderDTO dto = new OrderDTO();
        dto.setId(null);
        dto.setProduct("test-product");
        dto.setQuantity(2);

        Order order = new Order();
        when(orderMapper.toOrder(any(OrderDTO.class))).thenReturn(order);
        when(orderService.create(any(Order.class))).thenReturn("42");

        mockMvc.perform(
                post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string("42"));

        // verify interactions and captured arguments
        ArgumentCaptor<OrderDTO> dtoCaptor = ArgumentCaptor.forClass(OrderDTO.class);
        verify(orderMapper, times(1)).toOrder(dtoCaptor.capture());
        assertEquals("test-product", dtoCaptor.getValue().getProduct());

        verify(orderService, times(1)).create(any(Order.class));
    }

    @Test
    void create_invalidDto_returns400_andDoesNotCallService() throws Exception {
        // missing product and invalid quantity -> should fail validation
        OrderDTO dto = new OrderDTO();
        dto.setQuantity(0);

        mockMvc.perform(
                post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());

        verify(orderMapper, never()).toOrder(any());
        verify(orderService, never()).create(any());
    }

}
