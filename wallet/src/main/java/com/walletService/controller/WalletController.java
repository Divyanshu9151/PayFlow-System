package com.walletService.controller;



import com.walletService.dto.TransactionResponse;
import com.walletService.dto.WalletCreditRequest;
import com.walletService.dto.WalletDebitRequest;
import com.walletService.dto.WalletResponse;
import com.walletService.repository.WalletTransactionRepository;
import com.walletService.service.WalletService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("api/wallets")
public class WalletController {

    private final WalletService walletService;
    private final WalletTransactionRepository walletTransactionRepository;
    public WalletController(WalletService walletService, WalletTransactionRepository walletTransactionRepository) {
        this.walletService = walletService;
        this.walletTransactionRepository = walletTransactionRepository;
    }
    @GetMapping("/{walletId}/transactions")
    public List<TransactionResponse> getTransactions(@PathVariable Long walletId) {
        return walletTransactionRepository.findByWalletId(walletId)
                .stream()
                .map(TransactionResponse::new)
                .toList();
    }

    @PostMapping("/{id}/credit")
    public ResponseEntity<?> credit(@PathVariable Long id, @RequestHeader("Idempotence-Key") String key, @Valid @RequestBody WalletCreditRequest req)
    {
        walletService.credit(id,req.getAmount(),key,req.getDescription());
        return ResponseEntity.ok(Map.of("status","credited"));
    }

    @PostMapping("/{id}/debit")
    public ResponseEntity<?>debit(@PathVariable Long id,@RequestHeader("Idempotence-Key") String key ,@Valid @RequestBody WalletDebitRequest req)
    {
        walletService.debit(id,req.getAmount(),key,req.getDescription());
        return ResponseEntity.ok(Map.of("status","debited"));
    }
    @GetMapping
    public List<WalletResponse>getAllWallets(){
        return walletService.getAllWallet();
    }
    @GetMapping("/{id}/balance")
    public ResponseEntity<?>getWalletBalance(@PathVariable Long id)
    {
        BigDecimal balance=walletService.getWalletBalance(id);
        return ResponseEntity.ok(new WalletResponse(id,balance));
    }
    @PostMapping("/create")
    public Long getWalletId()
    {
        Long walletId=walletService.createWallet();
        return  walletId;
    }

}
