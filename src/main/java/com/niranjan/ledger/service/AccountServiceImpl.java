package com.niranjan.ledger.service;

import com.niranjan.ledger.dto.AccountDTO;
import com.niranjan.ledger.entity.Account;
import com.niranjan.ledger.exception.AccountNotFoundException;
import com.niranjan.ledger.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    @Override
    @Transactional
    public AccountDTO createAccount(AccountDTO accountDTO) {
        Account account = mapToEntity(accountDTO);
        Account savedAccount = accountRepository.save(account);
        return mapToDTO(savedAccount);
    }

    @Override
    @Transactional(readOnly = true)
    public AccountDTO getAccountById(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with id: " + id));
        return mapToDTO(account);
    }

    @Override
    @Transactional(readOnly = true)
    public AccountDTO getAccountByAccountNumber(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with account number: " + accountNumber));
        return mapToDTO(account);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountDTO> getAllAccounts() {
        return accountRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteAccount(Long id) {
        if (!accountRepository.existsById(id)) {
            throw new AccountNotFoundException("Account not found with id: " + id);
        }
        accountRepository.deleteById(id);
    }

    // Helper mapping methods
    private Account mapToEntity(AccountDTO dto) {
        return Account.builder()
                .id(dto.getId())
                .name(dto.getName())
                .accountNumber(dto.getAccountNumber())
                .balance(dto.getBalance())
                .build();
    }

    private AccountDTO mapToDTO(Account entity) {
        return AccountDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .accountNumber(entity.getAccountNumber())
                .balance(entity.getBalance())
                .build();
    }
}
