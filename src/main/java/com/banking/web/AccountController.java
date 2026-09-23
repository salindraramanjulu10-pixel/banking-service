package com.banking.web;

import com.banking.domain.Account;
import com.banking.domain.Transaction;
import com.banking.service.BankAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@Tag(name = "Bank Accounts", description = "Operations for account creation, balances, and transaction history")
public class AccountController {

    private final BankAccountService service;

    public AccountController(BankAccountService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Create a new account", description = "Creates a new bank account with an opening balance")
    public Account create(@Valid @RequestBody AccountRequest request) {
        return service.createAccount(request.accountId, request.amount);
    }

    @GetMapping("/{accountId}")
    @Operation(summary = "Get account balance", description = "Returns the current balance for a specific account")
    public Account getBalance(@PathVariable String accountId) {
        return service.getAccount(accountId);
    }

    @GetMapping("/{accountId}/transactions")
    @Operation(summary = "Get account history", description = "Returns all transaction records for one account")
    public List<Transaction> getTransactions(@PathVariable String accountId) {
        return service.getTransactions(accountId);
    }

    @PostMapping("/{accountId}/deposits")
    @Operation(summary = "Deposit funds", description = "Adds funds to the given account")
    public void deposit(@PathVariable String accountId, @Valid @RequestBody AmountRequest request) {
        service.deposit(accountId, request.amount);
    }

    @PostMapping("/{accountId}/withdrawals")
    @Operation(summary = "Withdraw funds", description = "Withdraws funds from the given account after checking balances")
    public void withdraw(@PathVariable String accountId, @Valid @RequestBody AmountRequest request) {
        service.withdraw(accountId, request.amount);
    }

    @PostMapping("/transfers")
    @Operation(summary = "Transfer funds", description = "Transfers funds between two accounts with validation and ledger entries")
    public void transfer(@Valid @RequestBody TransferRequest request) {
        service.transfer(request.sourceAccountId, request.destinationAccountId, request.amount);
    }

    public static class AccountRequest extends AmountRequest {
        @NotBlank
        public String accountId;
    }

    public static class AmountRequest {
        @NotNull
        public BigDecimal amount;
    }

    public static class TransferRequest extends AmountRequest {
        @NotBlank
        public String sourceAccountId;
        @NotBlank
        public String destinationAccountId;
    }
}