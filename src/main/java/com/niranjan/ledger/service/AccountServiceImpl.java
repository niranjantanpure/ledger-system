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




}
