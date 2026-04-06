package com.niranjan.ledger.service;

import com.niranjan.ledger.dto.TransactionDTO;
import com.niranjan.ledger.entity.Account;
import com.niranjan.ledger.exception.AccountNotFoundException;
import com.niranjan.ledger.exception.DuplicateTransactionException;
import com.niranjan.ledger.exception.InsufficientBalanceException;
import com.niranjan.ledger.repository.AccountRepository;
import com.niranjan.ledger.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class TransactionServiceTests {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    private Long fromAccountId;
    private Long toAccountId;

    @BeforeEach
    void setUp() {
        // Clear repositories
        transactionRepository.deleteAll();
        accountRepository.deleteAll();

        // Create initial accounts
        Account fromAccount = accountRepository.save(Account.builder()
                .name("Sender")
                .accountNumber("ACC001")
                .balance(new BigDecimal("1000.00"))
                .build());
        fromAccountId = fromAccount.getId();

        Account toAccount = accountRepository.save(Account.builder()
                .name("Receiver")
                .accountNumber("ACC002")
                .balance(new BigDecimal("500.00"))
                .build());
        toAccountId = toAccount.getId();
    }

    @Test
    void testSuccessfulTransfer() {
        String requestKey = UUID.randomUUID().toString();
        TransactionDTO request = TransactionDTO.builder()
                .fromAccountId(fromAccountId)
                .toAccountId(toAccountId)
                .amount(new BigDecimal("200.00"))
                .requestKey(requestKey)
                .build();

        TransactionDTO result = transactionService.transferFunds(request);

        assertNotNull(result.getId());
        assertEquals(fromAccountId, result.getFromAccountId());
        assertEquals(toAccountId, result.getToAccountId());
        assertEquals(new BigDecimal("200.00"), result.getAmount());

        Account fromAccount = accountRepository.findById(fromAccountId).get();
        Account toAccount = accountRepository.findById(toAccountId).get();

        assertEquals(new BigDecimal("800.00"), fromAccount.getBalance());
        assertEquals(new BigDecimal("700.00"), toAccount.getBalance());
    }

    @Test
    void testInsufficientBalance() {
        String requestKey = UUID.randomUUID().toString();
        TransactionDTO request = TransactionDTO.builder()
                .fromAccountId(fromAccountId)
                .toAccountId(toAccountId)
                .amount(new BigDecimal("2000.00"))
                .requestKey(requestKey)
                .build();

        assertThrows(InsufficientBalanceException.class, () -> {
            transactionService.transferFunds(request);
        });
    }

    @Test
    void testIdempotency() {
        String requestKey = "fixed-request-key";
        TransactionDTO request = TransactionDTO.builder()
                .fromAccountId(fromAccountId)
                .toAccountId(toAccountId)
                .amount(new BigDecimal("100.00"))
                .requestKey(requestKey)
                .build();

        transactionService.transferFunds(request);

        // Try same transaction again
        assertThrows(DuplicateTransactionException.class, () -> {
            transactionService.transferFunds(request);
        });
    }

    @Test
    void testSameAccountTransferFailure() {
        String requestKey = UUID.randomUUID().toString();
        TransactionDTO request = TransactionDTO.builder()
                .fromAccountId(fromAccountId)
                .toAccountId(fromAccountId)
                .amount(new BigDecimal("100.00"))
                .requestKey(requestKey)
                .build();

        assertThrows(IllegalArgumentException.class, () -> {
            transactionService.transferFunds(request);
        });
    }
}
