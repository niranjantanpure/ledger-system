package com.niranjan.ledger.service;

import com.niranjan.ledger.dto.AccountDTO;
import java.util.List;

public interface AccountService {
    AccountDTO createAccount(AccountDTO accountDTO);
    AccountDTO getAccountById(Long id);
    AccountDTO getAccountByAccountNumber(String accountNumber);
    List<AccountDTO> getAllAccounts();
    void deleteAccount(Long id);
}
