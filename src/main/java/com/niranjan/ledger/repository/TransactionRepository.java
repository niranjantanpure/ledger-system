package com.niranjan.ledger.repository;

import com.niranjan.ledger.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    boolean existsByRequestKey(String requestKey);
}
