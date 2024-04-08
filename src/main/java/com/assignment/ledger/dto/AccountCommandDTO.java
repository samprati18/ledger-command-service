package com.assignment.ledger.dto;

import com.assignment.ledger.entity.AccountState;
import lombok.Data;

@Data
public class AccountCommandDTO {
    private Long id;
    private String accountNumber;
    private Long entityId;
    private String entityName;
    private String name;
    private AccountState state;
}
