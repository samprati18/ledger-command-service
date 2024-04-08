package com.assignment.ledger.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
public class MultipleAssetMovementRequest {
    @Valid// This annotation will validate each item in the list
    @NotEmpty(message = "Asset movement list cannot be empty")
    private List<@NotNull(message = "Asset movement request cannot be null")AssetMovementRequest> assetMovementRequest;

    public MultipleAssetMovementRequest(){

    }
}
