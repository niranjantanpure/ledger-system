package com.niranjan.ledger.transaction;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record CreateTransactionRequest(
		@NotNull @DecimalMin(value = "0.01") BigDecimal amount,
		@NotNull TransactionType type,
		@NotBlank String category,
		@NotBlank String description,
		@NotNull OffsetDateTime occurredAt) {
}
