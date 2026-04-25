package com.niranjan.ledger.event;

import com.niranjan.ledger.dto.TransactionDTO;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class TransactionEvent extends ApplicationEvent {
    private final TransactionDTO transactionDTO;

    public TransactionEvent(Object source, TransactionDTO transactionDTO) {
        super(source);
        this.transactionDTO = transactionDTO;
    }
}
