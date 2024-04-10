package com.assignment.ledger.controller;


import com.assignment.ledger.dto.AccountStateChangeDTO;
import com.assignment.ledger.entity.AccountState;
import com.assignment.ledger.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AccountControllerTest {

    @Mock
    private AccountService accountService;

    @InjectMocks
    private AccountController accountController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void manageAccountLifecycle_ValidInput_ReturnsOk() {
        // Arrange
        AccountStateChangeDTO accountStateChangeDTO = new AccountStateChangeDTO();
        accountStateChangeDTO.setAccountId("accountId");
        accountStateChangeDTO.setAccountState(AccountState.CLOSED);

        when(accountService.changeAccountState(accountStateChangeDTO.getAccountId(), accountStateChangeDTO.getAccountState()))
                .thenReturn("Account state has been changed successfully");

        // Act
        ResponseEntity<String> response = accountController.manageAccountLifecycle(accountStateChangeDTO);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Account state has been changed successfully", response.getBody());
        verify(accountService, times(1)).changeAccountState(accountStateChangeDTO.getAccountId(), accountStateChangeDTO.getAccountState());
    }


}
