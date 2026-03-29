package com.niranjan.ledger.service;

import com.niranjan.ledger.dto.TransactionDTO;
import java.util.List;

public interface TransactionService {
    TransactionDTO transferFunds(TransactionDTO transactionDTO);
    TransactionDTO getTransactionById(Long id);
    List<TransactionDTO> getAllTransactions();
    List<TransactionDTO> getTransactionsByAccountId(Long accountId);
}
