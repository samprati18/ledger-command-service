package com.assignment.ledger.service;

import com.assignment.ledger.entity.AccountState;
import com.assignment.ledger.entity.command.AccountCommand;
import com.assignment.ledger.event.KafkaEventPublisher;
import com.assignment.ledger.exception.AccountNotFoundException;
import com.assignment.ledger.mapper.EntityMapper;
import com.assignment.ledger.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * The AccountService class manages the state changes of accounts and publishes events accordingly.
 */
@Service
public class AccountService {
    // Repository for accessing accounts
    private final AccountRepository accountRepository;
    // Event publisher for publishing events
    private final KafkaEventPublisher kafkaEventPublisher;

    // Mapper for mapping entity objects to DTOs
    private final EntityMapper entityMapper;

    /**
     * Constructs an instance of AccountService with the necessary dependencies.
     *
     * @param accountRepository   The repository for accounts
     * @param kafkaEventPublisher The Kafka event publisher for asynchronous event broadcasting
     * @param entityMapper        The mapper for entity-to-DTO mapping
     */
    @Autowired
    public AccountService(AccountRepository accountRepository, KafkaEventPublisher kafkaEventPublisher, EntityMapper entityMapper) {
        this.accountRepository = accountRepository;
        this.kafkaEventPublisher = kafkaEventPublisher;
        this.entityMapper = entityMapper;
    }
    /**
     * Changes the state of an account identified by the given accountNumber.
     * Publishes events after updating the state.
     *
     * @param accountNumber The account number of the account to be updated
     * @param newState      The new state to be set
     * @throws IllegalArgumentException if the account is not found with the given accountNumber
     */
    public String changeAccountState(String accountNumber, AccountState newState) {
        // Retrieve the account by accountNumber
        AccountCommand accountCommand = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with id: " + accountNumber));
        // Update the state of the accountCommand
        accountCommand.setState(newState);
        // Save the updated accountCommand
        accountRepository.save(accountCommand);
        // Publish event to Kafka
        kafkaEventPublisher.publishCommandEvents("ledger-account-state-change-event", entityMapper.toDTO(accountCommand));
        return "Account state has been changed successfully";
    }

}
