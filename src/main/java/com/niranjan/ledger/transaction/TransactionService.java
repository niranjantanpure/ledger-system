package com.niranjan.ledger.transaction;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {

	private final LedgerTransactionRepository transactionRepository;

	public TransactionService(LedgerTransactionRepository transactionRepository) {
		this.transactionRepository = transactionRepository;
	}

	public TransactionResponse create(CreateTransactionRequest request) {
		LedgerTransaction transaction = new LedgerTransaction(
				request.amount(),
				request.type(),
				request.category().trim(),
				request.description().trim(),
				request.occurredAt());

		return TransactionResponse.from(transactionRepository.save(transaction));
	}

	public List<TransactionResponse> findAll() {
		return transactionRepository.findAll(Sort.by(Sort.Direction.DESC, "occurredAt"))
				.stream()
				.map(TransactionResponse::from)
				.toList();
	}
}
