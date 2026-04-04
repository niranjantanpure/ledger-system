package com.niranjan.ledger.service;
 
import com.niranjan.ledger.dto.AccountDTO;
import com.niranjan.ledger.entity.Account;
import com.niranjan.ledger.exception.AccountAlreadyExistsException;
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
        // Business Rule: Check if account already exists
        if (accountRepository.findByAccountNumber(accountDTO.getAccountNumber()).isPresent()) {
            throw new AccountAlreadyExistsException("Account with number " + accountDTO.getAccountNumber() + " already exists.");
        }
 
        Account account = mapToEntity(accountDTO);
        Account savedAccount = accountRepository.save(account);
        return mapToDTO(savedAccount);
    }
 
    @Override
    @Transactional(readOnly = true)
    public AccountDTO getAccountById(Long id) {
        return accountRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with ID: " + id));
    }
 
    @Override
    @Transactional(readOnly = true)
    public AccountDTO getAccountByAccountNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .map(this::mapToDTO)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with number: " + accountNumber));
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
            throw new AccountNotFoundException("Cannot delete: Account not found with ID: " + id);
        }
        accountRepository.deleteById(id);
    }
 
    // --- Helper Methods (Mapping) ---
 
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
