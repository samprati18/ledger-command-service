package com.assignment.ledger.controller;

import com.assignment.ledger.dto.AssetMovementRequest;
import com.assignment.ledger.dto.MultipleAssetMovementRequest;
import com.assignment.ledger.service.AssetMovementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AssetMovementControllerTest {

    @Mock
    private AssetMovementService assetMovementService;

    @InjectMocks
    private AssetMovementController assetMovementController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void moveAssets_ValidInput_ReturnsOk() {
        // Arrange
        AssetMovementRequest assetMovementRequest = new AssetMovementRequest();
        assetMovementRequest.setSourceWalletId(1L);
        assetMovementRequest.setDestinationWalletId(2L);
        assetMovementRequest.setAmount(100.00);

        // Act
        ResponseEntity<String> response = assetMovementController.moveAssets(assetMovementRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Assets moved successfully", response.getBody());
        verify(assetMovementService, times(1)).moveAssets(assetMovementRequest.getSourceWalletId(), assetMovementRequest.getDestinationWalletId(), assetMovementRequest.getAmount());
    }

    @Test
    void moveMultipleAssets_ValidInput_ReturnsOk() {
        // Arrange
        MultipleAssetMovementRequest multipleAssetMovementRequest = new MultipleAssetMovementRequest();

        // Act
        ResponseEntity<String> response = assetMovementController.moveMultipleAssets(multipleAssetMovementRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Multiple Assets moved successfully", response.getBody());
        verify(assetMovementService, times(1)).moveMultipleAssets(multipleAssetMovementRequest);
    }

}
