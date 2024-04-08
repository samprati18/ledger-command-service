package com.assignment.ledger.event;

import com.assignment.ledger.entity.command.MovementCommand;
import com.assignment.ledger.entity.command.WalletCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class EventPublisherTest {

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    private EventPublisher eventPublisher;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void publishBalanceChangeEvent_ShouldPublishBalanceChangeEvent() {
        // Arrange
        WalletCommand wallet = new WalletCommand();

        // Act
        CompletableFuture<Void> future = eventPublisher.publishBalanceChangeEvent(wallet);

        // Assert
        verify(applicationEventPublisher).publishEvent(any(BalanceChangeEvent.class));
        future.join(); // Ensure that CompletableFuture completes
        assertEquals(CompletableFuture.completedFuture(null).join(), future.join()); // Ensure that CompletableFuture completes with null
    }

    @Test
    void publishMovementEvent_ShouldPublishMovementEvent() {
        // Arrange
        MovementCommand movement = new MovementCommand();

        // Act
        CompletableFuture<Void> future = eventPublisher.publishMovementEvent(movement);

        // Assert
        verify(applicationEventPublisher).publishEvent(any(MovementEvent.class));
        future.join(); // Ensure that CompletableFuture completes
        assertEquals(CompletableFuture.completedFuture(null).join(), future.join()); // Ensure that CompletableFuture completes with null
    }

}
