package com.example.interview.week2.utils;

import com.example.interview.week2.exception.ResourceNotFoundException;

public class RestPreconditions {
    
    public static <T> T checkNotFound(final T resource) {
        if (resource == null) {
            throw new ResourceNotFoundException();
        }
        return resource;
    }
}
