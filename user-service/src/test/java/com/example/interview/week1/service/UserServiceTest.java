package com.example.interview.week1.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import com.example.interview.week1.model.User;
import com.example.interview.week1.repository.UserRepository;
import com.example.interview.week1.service.impl.UserServiceImpl;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = new User();
        // try to set id/username if setters exist; otherwise rely on constructor
        try {
            sampleUser.setId("1");
            sampleUser.setUsername("alice");
        } catch (Exception e) {
            // ignore if setters not available; tests will still verify returned object
        }
    }

    @Test
    void findById_whenUserExists_returnsUser() {
        when(userRepository.findById("1")).thenReturn(Optional.of(sampleUser));

        User result = userService.findById("1");

        assertNotNull(result, "Expected non-null user when repository returns a user");
        assertEquals(sampleUser, result, "Expected returned user to match the repository user");
        verify(userRepository, times(1)).findById("1");
    }

    @Test
    void findById_whenUserNotFound_returnsNull() {
        when(userRepository.findById("2")).thenReturn(Optional.empty());

        User result = userService.findById("2");

        assertNull(result, "Expected null when repository returns empty");
        verify(userRepository, times(1)).findById("2");
    }

    @Test
    void findByUsername_whenUserExists_returnsUser() {
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(sampleUser));

        User result = userService.findByUsername("alice");

        assertNotNull(result, "Expected non-null user when repository returns a user");
        assertEquals(sampleUser, result, "Expected returned user to match the repository user");
        verify(userRepository, times(1)).findByUsername("alice");
    }

    @Test
    void findByUsername_whenUserNotFound_returnsNull() {
        when(userRepository.findByUsername("bob")).thenReturn(Optional.empty());

        User result = userService.findByUsername("bob");

        assertNull(result, "Expected null when repository returns empty");
        verify(userRepository, times(1)).findByUsername("bob");
    }

    @Test
    void create_whenSaveSuccessful_returnsUser() {
        when(userRepository.save(sampleUser)).thenReturn(sampleUser);

        User result = userService.create(sampleUser);

        assertNotNull(result, "Expected non-null user when save is successful");
        assertEquals(sampleUser, result, "Expected returned user to match the saved user");
        verify(userRepository, times(1)).save(sampleUser);
    }

    @Test
    void create_whenDuplicateUser_throwsException() {
        when(userRepository.save(sampleUser)).thenThrow(new DataIntegrityViolationException("DB error"));

        assertThrows(DataIntegrityViolationException.class, () -> userService.create(sampleUser),
                "Expected exception when saving duplicate user");
        verify(userRepository, times(1)).save(sampleUser);
    }
}
