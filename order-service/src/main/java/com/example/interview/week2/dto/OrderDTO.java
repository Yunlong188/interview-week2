package com.example.interview.week2.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private String id;
    @NotBlank(message = "User ID cannot be blank")
    private String userId;
    @Size(min = 1, message = "Order must contain at least one item")
    @Valid
    private List<OrderItemDTO> items;
}
