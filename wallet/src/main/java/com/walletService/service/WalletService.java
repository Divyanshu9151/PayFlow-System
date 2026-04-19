package com.walletService.service;


import com.walletService.dto.WalletResponse;
import com.walletService.entity.IdempotencyKey;
import com.walletService.entity.WalletTransaction;
import com.walletService.entity.Wallet;
import com.walletService.enums.TransactionType;
import com.walletService.exception.InsufficientBalanceException;
import com.walletService.messaging.TransactionMessage;
import com.walletService.messaging.TransactionProducer;
import com.walletService.repository.IdempotencyRepository;
import com.walletService.repository.WalletTransactionRepository;
import com.walletService.repository.WalletRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;


@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final WalletTransactionRepository walletTransactionRepository;
    private final IdempotencyRepository idempotencyRepository;
    private final TransactionProducer transactionProducer;



    // ✅ CREDIT
    @CacheEvict(value = "walletBalance", key = "#walletId")
    public void credit(Long walletId, BigDecimal amount, String idempotenceKey, String description) {

        log.info("Credit request received | walletId={} | amount={}", walletId, amount);

        Wallet wallet = getWallet(walletId);

        BigDecimal newBalance = wallet.getBalance().add(amount);
        wallet.setBalance(newBalance);

        // ✅ Save transaction FIRST
        WalletTransaction txn = walletTransactionRepository.save(
                new WalletTransaction(wallet, TransactionType.CREDIT, amount, newBalance, null, description)
        );

        // ✅ Send message using DTO
        transactionProducer.sendTransaction(
                new TransactionMessage(walletId,amount, description,"CREDIT")
        );

        // ✅ Idempotency
        idempotencyRepository.save(new IdempotencyKey(idempotenceKey, hash(walletId, amount)));


        log.info("Credit successful | walletId={} | newBalance={}", walletId, newBalance);
    }

    // ✅ DEBIT
    @CacheEvict(value = "walletBalance", key = "#walletId")
    public void debit(Long walletId, BigDecimal amount, String idempotencyKey, String description) {

        log.info("Debit request received | walletId={} | amount={}", walletId, amount);

        Wallet wallet = getWallet(walletId);

        if (wallet.getBalance().compareTo(amount) < 0) {
            log.warn("Debit failed - insufficient balance | walletId={} | balance={} | attempted={}",
                    walletId, wallet.getBalance(), amount);
            throw new InsufficientBalanceException("Not enough balance");
        }

        BigDecimal newBalance = wallet.getBalance().subtract(amount);
        wallet.setBalance(newBalance);

        // ✅ Save transaction FIRST
        WalletTransaction txn = walletTransactionRepository.save(
                new WalletTransaction(wallet, TransactionType.DEBIT, amount, newBalance, null, description)
        );

        // ✅ Send message using DTO
        transactionProducer.sendTransaction(
                new TransactionMessage(walletId,amount, description,"DEBIT")
        );

        // ✅ Idempotency
        idempotencyRepository.save(new IdempotencyKey(idempotencyKey, hash(walletId, amount)));
        log.info("Debit successful | walletId={} | newBalance={}", walletId, newBalance);
    }

    private String hash(Long walletId, BigDecimal amount) {
        return walletId + ":" + amount;
    }

    public List<WalletResponse> getAllWallet() {
        return walletRepository.findAll()
                .stream()
                .map(wallet -> new WalletResponse(wallet.getId(), wallet.getBalance()))
                .toList();
    }

    public Wallet getWallet(Long walletId) {
        return walletRepository.findById(walletId)
                .orElseThrow(() -> new EntityNotFoundException("Wallet Not Found"));
    }
    public Long createWallet()
    {
        Wallet wallet=new Wallet();
        wallet.setBalance(BigDecimal.ZERO);
        Wallet savedWallet=walletRepository.save(wallet);
        return savedWallet.getId();
    }


    @Cacheable(value = "walletBalance", key = "#walletId")
    public BigDecimal getWalletBalance(Long walletId) {
        log.info("Fetching balance from DB for walletId={}", walletId);
        return walletRepository.findBalanceById(walletId)
                .orElseThrow(() -> new EntityNotFoundException("Wallet Not Found"));
    }
}