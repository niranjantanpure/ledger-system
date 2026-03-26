package com.niranjan.ledger.repository;
import com.niranjan.ledger.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;


public interface TransactionRepository extends JpaRepository<Transaction, Long>{
    boolean existsRequestKey(String requestKey);
}
