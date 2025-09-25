package com.example.interview.week2.controller.it;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.interview.week2.repository.OrderRepository;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private OrderRepository orderRepository;

    @BeforeEach
    void setUp() throws Exception {
        orderRepository.deleteAll();
    }

    @Test
    void findById_whenOrderExists_thenSuccess() throws Exception {
        mockMvc.perform(post("/orders").contentType(MediaType.APPLICATION_JSON)
                .content("{\"product\":\"Widget\",\"quantity\":5}"))
                .andExpect(status().isOk())
                .andDo(handler -> {
                    String id = handler.getResponse().getContentAsString();
                    mockMvc.perform(get("/orders/" + id)
                            .accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().isOk());
                });
    }

    @Test
    void findById_whenNotFound_returns404() throws Exception {
        mockMvc.perform(get("/orders/9").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_whenValidDto_thenSuccess() throws Exception {
        mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"product\":\"Widget\",\"quantity\":5}"))
                .andExpect(status().isOk());
    }

    @Test
    void create_whenInvalidDto_thenBadRequest() throws Exception {
        mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"product\":\"\",\"quantity\":0}"))
                .andExpect(status().isBadRequest());
    }
}
