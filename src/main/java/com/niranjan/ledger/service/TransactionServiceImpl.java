package com.niranjan.ledger.service;

import com.niranjan.ledger.dto.TransactionDTO;
import com.niranjan.ledger.entity.Account;
import com.niranjan.ledger.entity.Transaction;
import com.niranjan.ledger.exception.AccountNotFoundException;
import com.niranjan.ledger.exception.DuplicateTransactionException;
import com.niranjan.ledger.exception.InsufficientBalanceException;
import com.niranjan.ledger.repository.AccountRepository;
import com.niranjan.ledger.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    @Override
    @Transactional
    public TransactionDTO transferFunds(TransactionDTO transactionDTO) {
        // 1. Validate different accounts
        if (transactionDTO.getFromAccountId().equals(transactionDTO.getToAccountId())) {
            throw new IllegalArgumentException("Source and destination accounts must be different");
        }

        // 2. Idempotency check
        if (transactionRepository.existsByRequestKey(transactionDTO.getRequestKey())) {
            throw new DuplicateTransactionException("Transaction with request key already exists: " + transactionDTO.getRequestKey());
        }

        // 3. Find accounts
        Account fromAccount = accountRepository.findById(transactionDTO.getFromAccountId())
                .orElseThrow(() -> new AccountNotFoundException("Source account not found with id: " + transactionDTO.getFromAccountId()));

        Account toAccount = accountRepository.findById(transactionDTO.getToAccountId())
                .orElseThrow(() -> new AccountNotFoundException("Destination account not found with id: " + transactionDTO.getToAccountId()));

        // 4. Check balance
        if (fromAccount.getBalance().compareTo(transactionDTO.getAmount()) < 0) {
            throw new InsufficientBalanceException("Insufficient funds in account: " + fromAccount.getAccountNumber());
        }

        // 5. Update balances
        fromAccount.setBalance(fromAccount.getBalance().subtract(transactionDTO.getAmount()));
        toAccount.setBalance(toAccount.getBalance().add(transactionDTO.getAmount()));

        // 6. Save accounts
        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        // 7. Create and save transaction
        Transaction transaction = Transaction.builder()
                .fromAccountId(transactionDTO.getFromAccountId())
                .toAccountId(transactionDTO.getToAccountId())
                .amount(transactionDTO.getAmount())
                .requestKey(transactionDTO.getRequestKey())
                .build();

        Transaction savedTransaction = transactionRepository.save(transaction);

        return mapToDTO(savedTransaction);
    }

    @Override
    @Transactional(readOnly = true)
    public TransactionDTO getTransactionById(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + id));
        return mapToDTO(transaction);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionDTO> getAllTransactions() {
        return transactionRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionDTO> getTransactionsByAccountId(Long accountId) {
        // Need to add this method to TransactionRepository if needed,
        // but for now I'll filter from all or implement findByAccountId in repository.
        // Let's keep it simple for now or update Repository.
        return transactionRepository.findAll().stream()
                .filter(t -> t.getFromAccountId().equals(accountId) || t.getToAccountId().equals(accountId))
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private TransactionDTO mapToDTO(Transaction entity) {
        return TransactionDTO.builder()
                .id(entity.getId())
                .fromAccountId(entity.getFromAccountId())
                .toAccountId(entity.getToAccountId())
                .amount(entity.getAmount())
                .timestamp(entity.getTimestamp())
                .requestKey(entity.getRequestKey())
                .build();
    }
}
