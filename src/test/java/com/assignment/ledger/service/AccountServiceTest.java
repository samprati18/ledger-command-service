package com.assignment.ledger.service;

import com.assignment.ledger.entity.AccountState;
import com.assignment.ledger.entity.command.AccountCommand;
import com.assignment.ledger.entity.command.EntityCommand;
import com.assignment.ledger.event.KafkaEventPublisher;
import com.assignment.ledger.repository.AccountRepository;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.mockito.Mockito.*;

class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private KafkaEventPublisher kafkaEventPublisher;

    @InjectMocks
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void changeAccountState_AccountFound_ShouldChangeStateAndPublishEvent() {
        // Arrange
        String accountNumber = "123";
        AccountState newState = AccountState.OPEN;
        AccountCommand account = new AccountCommand(1L, accountNumber, new EntityCommand(),"Account 1",AccountState.CLOSED);
        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));

        // Act
        accountService.changeAccountState(accountNumber, newState);

        // Assert
        verify(accountRepository).findByAccountNumber(accountNumber);
        Assert.assertEquals(newState, account.getState());
        verify(accountRepository).save(account);
        verify(kafkaEventPublisher).publishCommandEvents(eq("ledger-account-state-change"), any(AccountCommand.class));
    }

    @Test
    void changeAccountState_AccountNotFound_ShouldThrowException() {
        // Arrange
        String accountNumber = "123";
        AccountState newState = AccountState.OPEN;
        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.empty());

        // Act & Assert
        Assert.assertThrows(IllegalArgumentException.class, () -> {
            accountService.changeAccountState(accountNumber, newState);
        });

        // Verify
        verify(accountRepository).findByAccountNumber(accountNumber);
        verify(accountRepository, never()).save(any(AccountCommand.class));
        verify(kafkaEventPublisher, never()).publishCommandEvents(anyString(), any(AccountCommand.class));
    }
}
