package com.assignment.ledger.controller;

import com.assignment.ledger.dto.AssetMovementRequest;
import com.assignment.ledger.dto.MultipleAssetMovementRequest;
import com.assignment.ledger.service.AssetMovementService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;


@RestController
@Validated // Ensures that validation annotations are processed
@Slf4j
public class AssetMovementController {

    private final AssetMovementService assetMovementService;

    @Autowired
    public AssetMovementController(AssetMovementService assetMovementService) {
        this.assetMovementService = assetMovementService;
    }

    // Endpoint for moving a single asset
    @PostMapping("/moveAsset")
    public ResponseEntity<String> moveAssets(@Valid @RequestBody AssetMovementRequest assetMovementRequest) {
        assetMovementService.moveAssets(assetMovementRequest.getSourceWalletId(), assetMovementRequest.getDestinationWalletId(), assetMovementRequest.getAmount());
        return ResponseEntity.ok("Assets moved successfully");
    }


    // Endpoint for moving multiple assets
    @PostMapping("/moveMultipleAssets")
    public ResponseEntity<String> moveMultipleAssets(@Valid @RequestBody MultipleAssetMovementRequest request) {
        assetMovementService.moveMultipleAssets(request);
        return ResponseEntity.ok("Multiple Assets moved successfully");
    }
}
