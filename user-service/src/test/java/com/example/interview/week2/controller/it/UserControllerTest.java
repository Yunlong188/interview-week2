package com.example.interview.week2.controller.it;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.interview.week2.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void register_whenValidInput_createsUser() throws Exception {
        String userJson = """
                {
                    "username": "testuser",
                    "password": "Password1"
                }
                """;
        mockMvc.perform(
                post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void register_whenUsernameExists_returnsConflict() throws Exception {
        String userJson = """
                {
                    "username": "existinguser",
                    "password": "Password1"
                }
                """;
        mockMvc.perform(
                post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk());

        mockMvc.perform(
                post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_whenInvalidPassword_returnsBadRequest() throws Exception {
        String userJson = """
                {
                    "username": "newuser",
                    "password": "short"
                }
                """;
        mockMvc.perform(
                post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_whenValidCredentials_returnsUser() throws Exception {
        String registerJson = """
                {
                    "username": "loginuser",
                    "password": "Password1"
                }
                """;
        mockMvc.perform(
                post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson))
                .andExpect(status().isOk());

        String loginJson = """
                {
                    "username": "loginuser",
                    "password": "Password1"
                }
                """;
        mockMvc.perform(
                post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.username").value("loginuser"));
    }

    @Test
    void login_whenInvalidCredentials_returnsBadRequest() throws Exception {
        String loginJson = """
                {
                    "username": "nonexistent",
                    "password": "WrongPass1"
                }
                """;
        mockMvc.perform(
                post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isNotFound());
    }

    @Test
    void login_whenWrongPassword_returnsBadRequest() throws Exception {
        String registerJson = """
                {
                    "username": "user2",
                    "password": "Password1"
                }
                """;
        mockMvc.perform(
                post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson))
                .andExpect(status().isOk());

        String loginJson = """
                {
                    "username": "user2",
                    "password": "WrongPass1"
                }
                """;
        mockMvc.perform(
                post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isBadRequest());
    }
}
