package com.banking.service;

import com.banking.domain.Account;
import com.banking.domain.Transaction;
import com.banking.domain.TransactionType;
import com.banking.exception.AccountNotFoundException;
import com.banking.exception.InsufficientFundsException;
import com.banking.exception.InvalidAmountException;
import com.banking.repository.AccountRepository;
import com.banking.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
public class BankAccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public BankAccountService(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public Account createAccount(String accountId, BigDecimal openingBalance) {
        if (accountId == null || accountId.trim().isEmpty()) {
            throw new IllegalArgumentException("Account ID must not be blank");
        }
        validateAmount(openingBalance, true);
        if (accountRepository.existsById(accountId)) {
            throw new IllegalArgumentException("Account already exists: " + accountId);
        }
        return accountRepository.save(new Account(accountId, openingBalance));
    }

    @Transactional
    public void deposit(String accountId, BigDecimal amount) {
        validateAmount(amount, false);
        Account account = getAccount(accountId);
        account.credit(amount);
        accountRepository.save(account);
        record(account, TransactionType.DEPOSIT, amount);
    }

    @Transactional
    public void withdraw(String accountId, BigDecimal amount) {
        validateAmount(amount, false);
        Account account = getAccount(accountId);
        ensureFunds(account, amount);
        account.debit(amount);
        accountRepository.save(account);
        record(account, TransactionType.WITHDRAWAL, amount);
    }

    @Transactional
    public void transfer(String sourceId, String destinationId, BigDecimal amount) {
        validateAmount(amount, false);
        if (sourceId.equals(destinationId)) {
            throw new IllegalArgumentException("Source and destination accounts must differ");
        }
        Account source = getAccount(sourceId);
        Account destination = getAccount(destinationId);
        ensureFunds(source, amount);
        source.debit(amount);
        destination.credit(amount);
        accountRepository.save(source);
        accountRepository.save(destination);
        record(source, TransactionType.TRANSFER_OUT, amount);
        record(destination, TransactionType.TRANSFER_IN, amount);
    }

    @Transactional(readOnly = true)
    public Account getAccount(String accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
    }

    @Transactional(readOnly = true)
    public List<Transaction> getTransactions(String accountId) {
        getAccount(accountId);
        return transactionRepository.findByAccountIdOrderByTimestampAsc(accountId);
    }

    private void ensureFunds(Account account, BigDecimal amount) {
        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException(account.getId());
        }
    }

    private void validateAmount(BigDecimal amount, boolean allowZero) {
        if (amount == null || amount.scale() > 4 || amount.compareTo(BigDecimal.ZERO) < 0
                || (!allowZero && amount.compareTo(BigDecimal.ZERO) == 0)) {
            throw new InvalidAmountException();
        }
    }

    private void record(Account account, TransactionType type, BigDecimal amount) {
        transactionRepository.save(new Transaction(account, type, amount, Instant.now()));
    }
}