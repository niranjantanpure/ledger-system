package com.niranjan.ledger.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    public static final String TRANSACTIONS_TOPIC = "transactions-topic";
    public static final String ACCOUNTS_TOPIC = "accounts-topic";

    @Bean
    public NewTopic transactionsTopic() {
        return TopicBuilder.name(TRANSACTIONS_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic accountsTopic() {
        return TopicBuilder.name(ACCOUNTS_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
