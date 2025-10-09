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
import org.springframework.web.client.RestTemplate;
import org.springframework.test.web.client.MockRestServiceServer;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.example.interview.week2.repository.OrderRepository;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private RestTemplate restTemplate;
    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() throws Exception {
        orderRepository.deleteAll();
        mockServer = MockRestServiceServer.createServer(restTemplate);
        mockServer.expect(requestTo("http://host.docker.internal:8082/api/users/1"))
                .andRespond(withSuccess("{\"id\":\"1\"}", MediaType.APPLICATION_JSON));
    }

    @Test
    void findById_whenOrderExists_thenSuccess() throws Exception {
        mockMvc.perform(post("/orders").contentType(MediaType.APPLICATION_JSON)
                .content("{\"userId\":\"1\",\"items\":[{\"product\":\"Widget\",\"quantity\":5}]}"))
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
                .content("{\"userId\":\"1\",\"items\":[{\"product\":\"Widget\",\"quantity\":5}]}"))
                .andExpect(status().isOk());
    }

    @Test
    void create_whenInvalidDto_thenBadRequest() throws Exception {
        mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"userId\":\"1\",\"items\":[{\"product\":\"\",\"quantity\":0}]}"))
                .andExpect(status().isBadRequest());
    }
}
