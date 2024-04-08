package com.assignment.ledger.dto;

import lombok.Data;

@Data
public class WalletCommandDTO {
    private Long id;
    private String name;
    private Long accountId;
    private Long assetId;
    private double balance;
}
