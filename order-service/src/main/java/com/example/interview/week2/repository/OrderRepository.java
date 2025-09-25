package com.example.interview.week2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.interview.week2.model.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {

}
