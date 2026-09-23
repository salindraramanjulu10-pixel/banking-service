package com.banking.service;

import com.banking.domain.Account;
import com.banking.domain.Transaction;
import com.banking.domain.TransactionType;
import com.banking.exception.InsufficientFundsException;
import com.banking.repository.AccountRepository;
import com.banking.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BankAccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    private BankAccountService service;

    @BeforeEach
    void setUp() {
        service = new BankAccountService(accountRepository, transactionRepository);
    }

    @Test
    void depositIncreasesBalanceAndWritesLedgerEntry() {
        Account account = new Account("A-1", new BigDecimal("100.00"));
        when(accountRepository.findById("A-1")).thenReturn(Optional.of(account));

        service.deposit("A-1", new BigDecimal("25.50"));

        assertThat(account.getBalance()).isEqualByComparingTo("125.50");
        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(captor.capture());
        assertThat(captor.getValue().getType()).isEqualTo(TransactionType.DEPOSIT);
        assertThat(captor.getValue().getAmount()).isEqualByComparingTo("25.50");
    }

    @Test
    void withdrawalRejectsOverdraftWithoutChangingBalance() {
        Account account = new Account("A-1", new BigDecimal("100.00"));
        when(accountRepository.findById("A-1")).thenReturn(Optional.of(account));

        assertThatThrownBy(() -> service.withdraw("A-1", new BigDecimal("100.01")))
                .isInstanceOf(InsufficientFundsException.class);

        assertThat(account.getBalance()).isEqualByComparingTo("100.00");
    }

    @Test
    void transferUpdatesBothAccountsAndWritesTwoLedgerEntries() {
        Account source = new Account("A-1", new BigDecimal("100.00"));
        Account destination = new Account("A-2", new BigDecimal("20.00"));
        when(accountRepository.findById("A-1")).thenReturn(Optional.of(source));
        when(accountRepository.findById("A-2")).thenReturn(Optional.of(destination));

        service.transfer("A-1", "A-2", new BigDecimal("40.00"));

        assertThat(source.getBalance()).isEqualByComparingTo("60.00");
        assertThat(destination.getBalance()).isEqualByComparingTo("60.00");
        verify(transactionRepository, org.mockito.Mockito.times(2)).save(any(Transaction.class));
    }
}