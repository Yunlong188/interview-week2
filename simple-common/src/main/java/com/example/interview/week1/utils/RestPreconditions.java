package com.example.interview.week1.utils;

import com.example.interview.week1.exception.ResourceNotFoundException;

public class RestPreconditions {
    
    public static <T> T checkNotFound(final T resource) {
        if (resource == null) {
            throw new ResourceNotFoundException();
        }
        return resource;
    }
}
