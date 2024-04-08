package com.assignment.ledger.dto;

import com.assignment.ledger.entity.MovementState;
import lombok.Data;

@Data
public class MovementCommandDTO {
    private Long id;
    private Long sourceWalletId;
    private Long destinationWalletId;
    private double amount;
    private String timestamp;
    private MovementState state;
}
