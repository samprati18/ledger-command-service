package com.assignment.ledger.dto;

import lombok.Data;

@Data
public class HistoricalBalanceCommandDTO {
    private Long id;
    private Long walletId;
    private double balance;
    private String timestamp;
}
