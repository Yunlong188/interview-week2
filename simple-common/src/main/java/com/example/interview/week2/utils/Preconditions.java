package com.example.interview.week2.utils;

import com.example.interview.week2.exception.BadRequestException;

public class Preconditions {
    
    public static <T> T checkNotNull(final T reference) {
        if (reference == null) {
            throw new BadRequestException("Resource cannot be null");
        }
        return reference;
    }
}
