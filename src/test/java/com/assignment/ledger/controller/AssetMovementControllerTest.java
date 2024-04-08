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

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AssetMovementControllerTest {

    @Mock
    private AssetMovementService assetMovementService;

    @InjectMocks
    private AssetMovementController assetMovementController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void moveAssets_ValidRequest_ShouldReturnOk() {
        // Arrange
        AssetMovementRequest assetMovementRequest = new AssetMovementRequest(123L, 456L, 100);
        doNothing().when(assetMovementService).moveAssets(assetMovementRequest.getSourceWalletId(), assetMovementRequest.getDestinationWalletId(), assetMovementRequest.getAmount());

        // Act
        ResponseEntity<String> responseEntity = assetMovementController.moveAssets(assetMovementRequest);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Assets moved successfully", responseEntity.getBody());
        verify(assetMovementService).moveAssets(assetMovementRequest.getSourceWalletId(), assetMovementRequest.getDestinationWalletId(), assetMovementRequest.getAmount());
    }

    @Test
    void moveAssets_ExceptionThrown_ShouldReturnBadRequest() {
        // Arrange
        AssetMovementRequest assetMovementRequest = new AssetMovementRequest(123L, 456L, 100);
        String errorMessage = "Invalid request";
        doThrow(new RuntimeException(errorMessage)).when(assetMovementService).moveAssets(assetMovementRequest.getSourceWalletId(), assetMovementRequest.getDestinationWalletId(), assetMovementRequest.getAmount());

        // Act
        ResponseEntity<String> responseEntity = assetMovementController.moveAssets(assetMovementRequest);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(errorMessage, responseEntity.getBody());
        verify(assetMovementService).moveAssets(assetMovementRequest.getSourceWalletId(), assetMovementRequest.getDestinationWalletId(), assetMovementRequest.getAmount());
    }

    @Test
    void moveMultipleAssets_ValidRequest_ShouldReturnOk() {
        // Arrange
        List<AssetMovementRequest> assetMovements = new ArrayList<>();
        assetMovements.add(new AssetMovementRequest(123L, 456L, 100));
        assetMovements.add(new AssetMovementRequest(789L, 1011L, 200));
        MultipleAssetMovementRequest request = new MultipleAssetMovementRequest(assetMovements);
        doNothing().when(assetMovementService).moveMultipleAssets(request);

        // Act
        ResponseEntity<String> responseEntity = assetMovementController.moveMultipleAssets(request);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Multiple Assets moved successfully", responseEntity.getBody());
        verify(assetMovementService).moveMultipleAssets(request);
    }

    @Test
    void moveMultipleAssets_ExceptionThrown_ShouldReturnBadRequest() {
        // Arrange
        List<AssetMovementRequest> assetMovements = new ArrayList<>();
        assetMovements.add(new AssetMovementRequest(123L, 456L, 100));
        assetMovements.add(new AssetMovementRequest(789L, 1011L, 200));
        MultipleAssetMovementRequest request = new MultipleAssetMovementRequest(assetMovements);
        String errorMessage = "Invalid request";
        doThrow(new RuntimeException(errorMessage)).when(assetMovementService).moveMultipleAssets(request);

        // Act
        ResponseEntity<String> responseEntity = assetMovementController.moveMultipleAssets(request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(errorMessage, responseEntity.getBody());
        verify(assetMovementService).moveMultipleAssets(request);
    }
}
