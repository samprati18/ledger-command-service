package com.assignment.ledger.dto;

import com.assignment.ledger.entity.AccountState;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AccountStateChangeDTO {
    @NotNull(message = "AccountId cannot be null")
    private String accountId;
    @NotNull(message = "AccountState cannot be null")
    private AccountState accountState;

}
