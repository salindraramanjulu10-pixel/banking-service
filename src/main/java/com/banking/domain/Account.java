package com.banking.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @Column(nullable = false, updatable = false)
    private String id;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal balance;

    protected Account() {
    }

    public Account(String id, BigDecimal openingBalance) {
        this.id = id;
        this.balance = openingBalance;
    }

    public String getId() {
        return id;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void credit(BigDecimal amount) {
        balance = balance.add(amount);
    }

    public void debit(BigDecimal amount) {
        balance = balance.subtract(amount);
    }
}