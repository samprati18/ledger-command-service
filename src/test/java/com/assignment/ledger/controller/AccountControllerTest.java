package com.assignment.ledger.controller;


import com.assignment.ledger.dto.AccountStateChangeDTO;
import com.assignment.ledger.entity.AccountState;
import com.assignment.ledger.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void manageAccountLifecycle_ValidDTO_Success() {
        // Arrange
        AccountStateChangeDTO accountStateChangeDTO = new AccountStateChangeDTO();
        accountStateChangeDTO.setAccountId("123");
        accountStateChangeDTO.setAccountState(AccountState.CLOSED);

        when(accountService.changeAccountState("123", AccountState.OPEN)).thenReturn("Success");

        // Act
        ResponseEntity<String> responseEntity = accountController.manageAccountLifecycle(accountStateChangeDTO);

        // Assert
        assertEquals("Success", responseEntity.getBody());
        assertEquals(200, responseEntity.getStatusCodeValue());

        // Verify
        verify(accountService, times(1)).changeAccountState("123", AccountState.OPEN);
    }

}
