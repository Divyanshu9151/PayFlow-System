package com.walletService.dto;



import com.walletService.entity.WalletTransaction;
import com.walletService.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionResponse {

    private Long id;
    private TransactionType type;
    private BigDecimal amount;
    private BigDecimal balanceAfter;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getBalanceAfter() {
        return balanceAfter;
    }

    public void setBalanceAfter(BigDecimal balanceAfter) {
        this.balanceAfter = balanceAfter;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }


    private LocalDateTime createdAt;
    public TransactionResponse(WalletTransaction walletTransaction) {
        this.id = walletTransaction.getId();
        this.amount = walletTransaction.getAmount();
        this.type = walletTransaction.getType();
        this.balanceAfter = walletTransaction.getBalanceAfter();
        this.createdAt = walletTransaction.getCreatedAt();
    }

}
