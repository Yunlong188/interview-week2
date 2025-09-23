package com.example.interview.week1.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.interview.week1.dto.RegistrationDTO;
import com.example.interview.week1.dto.UserDTO;
import com.example.interview.week1.dto.UsernamePasswordLoginDTO;
import com.example.interview.week1.exception.BadRequestException;
import com.example.interview.week1.mapper.UserMapper;
import com.example.interview.week1.model.User;
import com.example.interview.week1.service.UserService;
import com.example.interview.week1.utils.RestPreconditions;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserMapper userMapper;
    private final UserService userService;

    public UserController(UserMapper userMapper, UserService userService) {
        this.userMapper = userMapper;
        this.userService = userService;
    }

    @PostMapping("/register")
    public UserDTO register(@RequestBody @Valid RegistrationDTO registrationDTO) {
        User user = userService.create(userMapper.toEntity(registrationDTO));
        return userMapper.toDto(user);
    }

    @PostMapping("/login")
    public UserDTO login(@RequestBody @Valid UsernamePasswordLoginDTO loginDTO) {
        User user = userService.findByUsername(loginDTO.getUsername());
        RestPreconditions.checkNotFound(user);
        if (!user.getPassword().equals(loginDTO.getPassword())) {
            throw new BadRequestException("Invalid password");
        }
        return userMapper.toDto(user);
    }

}
