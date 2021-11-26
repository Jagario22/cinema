package com.epam.finalproject.cinema.domain.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

public class Wallet implements Serializable {
    private static final long serialVersionUID = 7858222769964022177L;
    private Integer id;
    private Integer userId;
    private BigDecimal balance;

    public Wallet(Integer id, Integer userId, BigDecimal balance) {
        this.id = id;
        this.userId = userId;
        this.balance = balance;
    }

    public Wallet(Integer userId, BigDecimal balance) {
        this.userId = userId;
        this.balance = balance;
    }

    public Integer getId() {
        return id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Wallet wallet = (Wallet) o;
        return userId.equals(wallet.userId) && balance.equals(wallet.balance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }

    @Override
    public String toString() {
        return "Wallet{" +
                "id=" + id +
                ", userId=" + userId +
                ", balance=" + balance +
                '}';
    }
}
