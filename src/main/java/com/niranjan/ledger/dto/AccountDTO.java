package com.niranjan.ledger.dto;

import jakarta.validation.Valid;
import lombok.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

@Valid
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class AccountDTO {

    private  Long id;

    private String name;

    @NotBlank(message = "Account number is required")
    private String accountNumber;

    @NotNull(message = "Balance is required")
    @PositiveOrZero(message = "Balance cannot be negative")
    private BigDecimal balance;

    private Long version;


}
