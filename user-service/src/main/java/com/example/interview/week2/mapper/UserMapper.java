package com.example.interview.week2.mapper;

import org.mapstruct.Mapper;

import com.example.interview.week2.dto.RegistrationDTO;
import com.example.interview.week2.dto.UserDTO;
import com.example.interview.week2.model.User;

@Mapper
public interface UserMapper {

    UserDTO toDto(User user);

    User toEntity(UserDTO userDTO);

    User toEntity(RegistrationDTO registrationDTO);
}
