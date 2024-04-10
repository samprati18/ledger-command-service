package com.assignment.ledger.service;


import com.assignment.ledger.entity.AccountState;
import com.assignment.ledger.entity.command.AccountCommand;
import com.assignment.ledger.event.KafkaEventPublisher;
import com.assignment.ledger.exception.AccountNotFoundException;
import com.assignment.ledger.exception.GeneralException;
import com.assignment.ledger.mapper.EntityMapper;
import com.assignment.ledger.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private KafkaEventPublisher kafkaEventPublisher;

    @Mock
    private EntityMapper entityMapper;

    @InjectMocks
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(accountService, "accountStateChangeEventTopic", "ledger-account-state-change-event");
    }

    @Test
    void testChangeAccountState_Success() {
        String accountNumber = "12345";
        AccountState newState = AccountState.OPEN;

        AccountCommand accountCommand = new AccountCommand();
        accountCommand.setAccountNumber(accountNumber);

        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(accountCommand));

        String result = accountService.changeAccountState(accountNumber, newState);

        assertEquals("Account state has been changed successfully", result);
        assertEquals(newState, accountCommand.getState());
        verify(accountRepository, times(1)).save(accountCommand);
        verify(kafkaEventPublisher, times(1)).publishCommandEvents(anyString(), any());
    }

    @Test
    void testChangeAccountState_AccountNotFound() {
        String accountNumber = "12345";
        AccountState newState = AccountState.OPEN;

        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> accountService.changeAccountState(accountNumber, newState));
        verify(accountRepository, never()).save(any());
        verify(kafkaEventPublisher, never()).publishCommandEvents(anyString(), any());
    }

    @Test
    void testChangeAccountState_ExceptionThrown() {
        String accountNumber = "12345";
        AccountState newState = AccountState.OPEN;

        AccountCommand accountCommand = new AccountCommand();
        accountCommand.setAccountNumber(accountNumber);

        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(accountCommand));
        doThrow(new RuntimeException("Test exception")).when(accountRepository).save(accountCommand);

        assertThrows(GeneralException.class, () -> accountService.changeAccountState(accountNumber, newState));
        verify(accountRepository, times(1)).save(accountCommand);
        verify(kafkaEventPublisher, never()).publishCommandEvents(anyString(), any());
    }

}
