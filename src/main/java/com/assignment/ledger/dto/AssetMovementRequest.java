package com.assignment.ledger.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Data
@Getter
@Setter
@AllArgsConstructor
public class AssetMovementRequest {
    @NotNull(message = "Source wallet ID cannot be null")
    private Long sourceWalletId;
    @NotNull(message = "Destination wallet ID cannot be null")
    private Long destinationWalletId;
    @Positive(message = "Amount must be positive")
    @NotNull
    private double amount;

    public AssetMovementRequest() {
        // Default constructor
    }

}
