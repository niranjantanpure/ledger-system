package com.niranjan.ledger.transaction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "ledger_transactions")
public class LedgerTransaction {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, precision = 19, scale = 2)
	private BigDecimal amount;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private TransactionType type;

	@Column(nullable = false, length = 100)
	private String category;

	@Column(nullable = false, length = 255)
	private String description;

	@Column(nullable = false)
	private OffsetDateTime occurredAt;

	protected LedgerTransaction() {
	}

	public LedgerTransaction(BigDecimal amount, TransactionType type, String category, String description,
			OffsetDateTime occurredAt) {
		this.amount = amount;
		this.type = type;
		this.category = category;
		this.description = description;
		this.occurredAt = occurredAt;
	}

	public Long getId() {
		return id;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public TransactionType getType() {
		return type;
	}

	public String getCategory() {
		return category;
	}

	public String getDescription() {
		return description;
	}

	public OffsetDateTime getOccurredAt() {
		return occurredAt;
	}
}
