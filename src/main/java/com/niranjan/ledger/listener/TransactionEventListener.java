package com.niranjan.ledger.listener;

import com.niranjan.ledger.dto.TransactionDTO;
import com.niranjan.ledger.config.KafkaConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TransactionEventListener {

    @KafkaListener(topics = KafkaConfig.TRANSACTIONS_TOPIC, groupId = "ledger-group")
    public void handleTransactionEvent(TransactionDTO transactionDTO) {
        log.info("Received Kafka message for transaction ID: {}", transactionDTO.getId());
        
        // Simulate side effects
        sendNotification(transactionDTO);
        logAuditTrail(transactionDTO);
    }

    private void sendNotification(TransactionDTO transactionDTO) {
        log.info("Sending notification: Amount {} transferred from Account {} to Account {}", 
            transactionDTO.getAmount(),
            transactionDTO.getFromAccountId(),
            transactionDTO.getToAccountId());
    }

    private void logAuditTrail(TransactionDTO transactionDTO) {
        log.info("Logging audit trail for Request Key: {}", transactionDTO.getRequestKey());
    }
}
