package com.niranjan.ledger.dto;

import jakarta.validation.Valid;
import lombok.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class TransactionDTO {

    private Long id;

    @NotNull(message = "Account Number required")
    private Long fromAccountId;

    @NotNull(message = "Account Number is required")
    private Long toAccountId;

    @NotNull(message = "Amount is required")
    @PositiveOrZero(message = "Amount must be positive")
    private BigDecimal amount;

    private LocalDateTime timestamp;

    @NotNull
    private String requestKey;



}
