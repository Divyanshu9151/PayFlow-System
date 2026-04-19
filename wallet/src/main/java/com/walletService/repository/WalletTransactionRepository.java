package com.walletService.repository;

import com.walletService.entity.WalletTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Long> {

    // ✅ Without pagination (simple list)
    List<WalletTransaction> findByWalletId(Long walletId);

    // ✅ With pagination (correct)
    Page<WalletTransaction> findByWalletId(Long walletId, Pageable pageable);
}