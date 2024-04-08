package com.assignment.ledger.service;

import com.assignment.ledger.entity.MovementState;
import com.assignment.ledger.entity.command.MovementCommand;
import com.assignment.ledger.event.EventPublisher;
import com.assignment.ledger.event.KafkaEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class PostingLifecycleServiceTest {

    @Mock
    private PostingRepository postingRepository;

    @Mock
    private EventPublisher eventPublisher;

    @Mock
    private KafkaEventPublisher kafkaEventPublisher;

    @InjectMocks
    private PostingLifecycleService postingLifecycleService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void setPostingState_PostingFound_ShouldUpdateStateAndPublishEvents() {
        // Arrange
        Long postingId = 1L;
        MovementState newState = MovementState.CLEARED;
        MovementCommand postingCommand = new MovementCommand();
        when(postingRepository.findById(postingId)).thenReturn(Optional.of(postingCommand));

        // Act
        postingLifecycleService.setPostingState(postingId, newState);

        // Assert
        verify(postingRepository).findById(postingId);
        verify(postingRepository).save(postingCommand);
        verify(eventPublisher).publishMovementEvent(postingCommand);
        verify(kafkaEventPublisher).publishCommandEvents(eq("ledger-asset-movement-event"), any());
    }

    @Test
    void setPostingState_PostingNotFound_ShouldThrowException() {
        // Arrange
        Long postingId = 1L;
        MovementState newState = MovementState.CLEARED;
        when(postingRepository.findById(postingId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            postingLifecycleService.setPostingState(postingId, newState);
        });

        // Verify
        verify(postingRepository).findById(postingId);
        verify(postingRepository, never()).save(any());
        verify(eventPublisher, never()).publishMovementEvent(any());
        verify(kafkaEventPublisher, never()).publishCommandEvents(anyString(), any());
    }

}
