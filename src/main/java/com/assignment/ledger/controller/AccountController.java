package com.assignment.ledger.controller;

import com.assignment.ledger.dto.AccountStateChangeDTO;
import com.assignment.ledger.service.AccountService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated // Ensures that validation annotations are processed
@Slf4j
public class AccountController {

    private AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PutMapping("/manageAccountLifecycle")
    public ResponseEntity<String> manageAccountLifecycle(@Valid @RequestBody AccountStateChangeDTO accountStateChangeDTO)  {
        return ResponseEntity.ok(accountService.changeAccountState(accountStateChangeDTO.getAccountId(), accountStateChangeDTO.getAccountState()));
    }

}
