package com.example.interview.week1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.interview.week1.model.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {

}
