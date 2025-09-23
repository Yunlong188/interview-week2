package com.example.interview.week1.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.example.interview.week1.model.User;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void whenFindByUsername_thenReturnUser() {
        // Given
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("testpass");
        userRepository.save(user);

        // When
        User found = userRepository.findByUsername("testuser").orElse(null);

        // Then
        assertNotNull(found);
        assertEquals("testuser", found.getUsername());
        assertEquals("testpass", found.getPassword());
    }

    @Test
    void whenFindByUsernameIsNotFound_thenReturnNull() {
        // When
        User found = userRepository.findByUsername("nonexistent").orElse(null);

        // Then
        assertNull(found);
    }
}
