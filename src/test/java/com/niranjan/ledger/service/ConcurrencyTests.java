package com.niranjan.ledger.service;

import com.niranjan.ledger.dto.TransactionDTO;
import com.niranjan.ledger.entity.Account;
import com.niranjan.ledger.repository.AccountRepository;
import com.niranjan.ledger.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
public class ConcurrencyTests {

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
        transactionRepository.deleteAll();
        accountRepository.deleteAll();

        Account fromAccount = accountRepository.save(Account.builder()
                .name("Sender")
                .accountNumber("SENDER-001")
                .balance(new BigDecimal("1000.00"))
                .build());
        fromAccountId = fromAccount.getId();

        Account toAccount = accountRepository.save(Account.builder()
                .name("Receiver")
                .accountNumber("RECEIVER-001")
                .balance(new BigDecimal("0.00"))
                .build());
        toAccountId = toAccount.getId();
    }

    @Test
    void testConcurrentTransfers() throws InterruptedException {
        int numberOfThreads = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        BigDecimal transferAmount = new BigDecimal("10.00");

        for (int i = 0; i < numberOfThreads; i++) {
            executorService.execute(() -> {
                try {
                    transactionService.transferFunds(TransactionDTO.builder()
                            .fromAccountId(fromAccountId)
                            .toAccountId(toAccountId)
                            .amount(transferAmount)
                            .requestKey(UUID.randomUUID().toString())
                            .build());
                    successCount.incrementAndGet();
                } catch (ObjectOptimisticLockingFailureException e) {
                    failureCount.incrementAndGet();
                } catch (Exception e) {
                    System.err.println("Unexpected error: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(10, TimeUnit.SECONDS);
        executorService.shutdown();

        // Refresh accounts from DB
        Account fromAccount = accountRepository.findById(fromAccountId).get();
        Account toAccount = accountRepository.findById(toAccountId).get();

        BigDecimal expectedFromBalance = new BigDecimal("1000.00").subtract(transferAmount.multiply(new BigDecimal(successCount.get())));
        BigDecimal expectedToBalance = new BigDecimal("0.00").add(transferAmount.multiply(new BigDecimal(successCount.get())));

        System.out.println("Successful transfers: " + successCount.get());
        System.out.println("Optimistic locking failures: " + failureCount.get());
        System.out.println("Final Source Balance: " + fromAccount.getBalance());

        // Verify that even if some failed due to optimistic locking, the balances are CONSISTENT
        assertEquals(expectedFromBalance.stripTrailingZeros(), fromAccount.getBalance().stripTrailingZeros(), "Source balance mismatch");
        assertEquals(expectedToBalance.stripTrailingZeros(), toAccount.getBalance().stripTrailingZeros(), "Destination balance mismatch");
        
        // Total sum should always be 1000.00
        assertEquals(new BigDecimal("1000.00").stripTrailingZeros(), 
                     fromAccount.getBalance().add(toAccount.getBalance()).stripTrailingZeros(), 
                     "Total balance sum mismatch (invariant violated)");
    }
}
