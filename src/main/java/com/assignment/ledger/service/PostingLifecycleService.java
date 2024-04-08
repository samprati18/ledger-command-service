package com.assignment.ledger.service;

import com.assignment.ledger.entity.MovementState;
import com.assignment.ledger.entity.command.MovementCommand;
import com.assignment.ledger.event.EventPublisher;
import com.assignment.ledger.event.KafkaEventPublisher;
import com.assignment.ledger.exception.PostingsNotFoundException;
import com.assignment.ledger.mapper.EntityMapper;
import com.assignment.ledger.repository.MovementCommandRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * The PostingLifecycleService handles the lifecycle state changes of movement commands.
 * It allows updating the state of a movement command and broadcasts events accordingly.
 */
@Service
@Slf4j
public class PostingLifecycleService {

    @PersistenceContext
    private EntityManager entityManager;
    private final EventPublisher eventPublisher;
    private final KafkaEventPublisher kafkaEventPublisher;
    private final EntityMapper entityMapper;


    @Autowired
    public PostingLifecycleService(EventPublisher eventPublisher, KafkaEventPublisher kafkaEventPublisher, EntityMapper entityMapper) {
        this.eventPublisher = eventPublisher;
        this.kafkaEventPublisher = kafkaEventPublisher;
        this.entityMapper = entityMapper;
    }

    /**
     * Sets the state of a movement command identified by the given postingId.
     * Broadcasts events after updating the state.
     *
     * @param postingId The ID of the movement command
     * @param newState  The new state to be set
     * @throws IllegalArgumentException if the movement command is not found with the given postingId
     */
    @Transactional
    public void setPostingState(Long postingId, MovementState newState) {
        int updatedRows = entityManager.createQuery(
                        "UPDATE MovementCommand mc SET mc.state = :newState WHERE mc.id = :postingId")
                .setParameter("newState", newState)
                .setParameter("postingId", postingId)
                .executeUpdate();

        if (updatedRows == 0) {
            throw new PostingsNotFoundException("Posting not found with id: " + postingId);
        }
        // Retrieve the updated MovementCommand directly from EntityManager
        MovementCommand updatedMovementCommand = entityManager.find(MovementCommand.class, postingId);
        if (updatedMovementCommand == null) {
            throw new PostingsNotFoundException("Posting not found with id: " + postingId);
        }
        // Broadcast movement event
        eventPublisher.publishMovementEvent(updatedMovementCommand);
        // Publish Kafka event
        kafkaEventPublisher.publishCommandEvents("ledger-asset-movement-event", entityMapper.toDTO(updatedMovementCommand));
        // Log the successful state update
        log.info("Posting state updated successfully for postingId: {}", postingId);
    }

}
