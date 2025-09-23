package com.example.interview.week1.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.interview.week1.dto.RegistrationDTO;
import com.example.interview.week1.dto.UserDTO;
import com.example.interview.week1.mapper.UserMapper;
import com.example.interview.week1.model.User;
import com.example.interview.week1.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserMapper userMapper;

    @Test
    void register_whenValidInput_callsServiceAndReturnsDto() throws Exception {
        RegistrationDTO registrationDTO = new RegistrationDTO();
        registrationDTO.setUsername("testuser");
        registrationDTO.setPassword("password");
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        User saved = new User();
        saved.setId("1");
        saved.setUsername("testuser");
        saved.setPassword("password");
        UserDTO dto = new UserDTO();
        dto.setId("1");
        dto.setUsername("testuser");

        when(userMapper.toEntity(any(RegistrationDTO.class))).thenReturn(user);
        when(userService.create(any(User.class))).thenReturn(saved);
        when(userMapper.toDto(any(User.class))).thenReturn(dto);

        mockMvc.perform(
                post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDTO)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));
    }

    @Test
    void register_whenInvalidInput_returnsBadRequest() throws Exception {
        RegistrationDTO registrationDTO = new RegistrationDTO();
        // Missing username and password

        mockMvc.perform(
                post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_whenValidCredentials_returnsDto() throws Exception {
        String username = "testuser";
        String password = "password";
        User user = new User();
        user.setId("1");
        user.setUsername(username);
        user.setPassword(password);
        UserDTO dto = new UserDTO();
        dto.setId("1");
        dto.setUsername(username);

        when(userService.findByUsername(username)).thenReturn(user);
        when(userMapper.toDto(any(User.class))).thenReturn(dto);

        String loginJson = """
                {
                    "username": "testuser",
                    "password": "password"
                }
                """;

        mockMvc.perform(
                post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));
    }

    @Test
    void login_whenUserNotFound_returnsNotFound() throws Exception {
        String username = "nonexistent";
        when(userService.findByUsername(username)).thenReturn(null);
        String loginJson = """
                {
                    "username": "nonexistent",
                    "password": "password"
                }
                """;
        mockMvc.perform(
                post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isNotFound());
    }

    @Test
    void login_whenInvalidPassword_returnsBadRequest() throws Exception {
        String username = "testuser";
        String correctPassword = "correctpassword";
        User user = new User();
        user.setId("1");
        user.setUsername(username);
        user.setPassword(correctPassword);

        when(userService.findByUsername(username)).thenReturn(user);

        String loginJson = """
                {
                    "username": "testuser",
                    "password": "wrongpassword"
                }
                """;

        mockMvc.perform(
                post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isBadRequest());
    }

}
