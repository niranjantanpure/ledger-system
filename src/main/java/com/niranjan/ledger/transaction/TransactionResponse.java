package com.niranjan.ledger.transaction;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record TransactionResponse(
		Long id,
		BigDecimal amount,
		TransactionType type,
		String category,
		String description,
		OffsetDateTime occurredAt) {

	public static TransactionResponse from(LedgerTransaction transaction) {
		return new TransactionResponse(
				transaction.getId(),
				transaction.getAmount(),
				transaction.getType(),
				transaction.getCategory(),
				transaction.getDescription(),
				transaction.getOccurredAt());
	}
}
