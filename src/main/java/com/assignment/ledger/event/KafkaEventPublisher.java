package com.assignment.ledger.event;

import com.assignment.ledger.entity.command.AccountCommand;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Async;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Configuration
@Slf4j
public class KafkaEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Async
    public CompletableFuture<Void> publishCommandEvents(String topicName, Object message) {
        System.out.println("Inside PublishCommandEvents ----- " + message);
        CompletableFuture<SendResult<String, Object>> send = kafkaTemplate.send(topicName, serializeToJson(message));
        try {
            log.info("Kafka record is published to topic {} at offset {} and partition {}",
                    send.get().getRecordMetadata().topic(),
                    send.get().getRecordMetadata().offset(),
                    send.get().getRecordMetadata().partition());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        return CompletableFuture.completedFuture(null);
    }

    private String serializeToJson(Object object) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            // Handle JSON processing exception
            e.printStackTrace(); // Or log the exception
            return null; // Or throw a custom exception
        }
    }


}
