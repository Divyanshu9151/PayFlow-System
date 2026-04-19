package com.walletService.messaging;

        import lombok.AllArgsConstructor;
        import lombok.Data;
        import lombok.NoArgsConstructor;

        import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionMessage {
    private Long walletId;
    private BigDecimal amount;
    private String description;
    private String type;
}