package com.example.interview.week2.service;

import com.example.interview.week2.model.User;

public interface UserService {

    User create(User user);

    User findById(String id);

    User findByUsername(String username);
}
