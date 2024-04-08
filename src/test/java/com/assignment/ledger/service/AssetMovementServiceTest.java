package com.assignment.ledger.service;

import com.assignment.ledger.dto.AssetMovementRequest;
import com.assignment.ledger.dto.MultipleAssetMovementRequest;
import com.assignment.ledger.entity.MovementState;
import com.assignment.ledger.entity.command.WalletCommand;
import com.assignment.ledger.event.EventPublisher;
import com.assignment.ledger.event.KafkaEventPublisher;
import com.assignment.ledger.repository.HistoricalBalanceCommandRepository;
import com.assignment.ledger.repository.MovementCommandRepository;
import com.assignment.ledger.repository.WalletCommandRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Optional;

import static org.mockito.Mockito.*;

class AssetMovementServiceTest {

    @Mock
    private MovementCommandRepository movementRepository;

    @Mock
    private WalletCommandRepository walletRepository;

    @Mock
    private HistoricalBalanceCommandRepository historicalBalanceRepository;

    @Mock
    private EventPublisher eventPublisher;

    @Mock
    private KafkaEventPublisher kafkaEventPublisher;

    @InjectMocks
    private AssetMovementService assetMovementService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void moveAssets_SourceAndDestinationWalletsFound_ShouldUpdateBalancesAndPublishEvents() {
        // Arrange
        Long sourceWalletId = 1L;
        Long destinationWalletId = 2L;
        double amount = 100.0;
        WalletCommand sourceWallet = new WalletCommand();
        sourceWallet.setId(sourceWalletId);
        sourceWallet.setBalance(200.0); // Sufficient balance
        WalletCommand destinationWallet = new WalletCommand();
        destinationWallet.setId(destinationWalletId);
        when(walletRepository.findById(sourceWalletId)).thenReturn(Optional.of(sourceWallet));
        when(walletRepository.findById(destinationWalletId)).thenReturn(Optional.of(destinationWallet));

        // Act
        assetMovementService.moveAssets(sourceWalletId, destinationWalletId, amount);

        // Assert
        verify(walletRepository, times(2)).findById(anyLong());
        verify(walletRepository, times(2)).save(any(WalletCommand.class));
        verify(movementRepository).save(any());
        verify(eventPublisher, times(2)).publishBalanceChangeEvent(any(WalletCommand.class));
        verify(eventPublisher).publishMovementEvent(any());
        verify(kafkaEventPublisher, times(3)).publishCommandEvents(anyString(), any());
    }

    @Test
    void moveMultipleAssets_ValidRequests_ShouldInvokeMoveAssetsForEachRequest() {
        // Arrange
        AssetMovementRequest request1 = new AssetMovementRequest(1L, 2L, 100.0);
        AssetMovementRequest request2 = new AssetMovementRequest(3L, 4L, 200.0);
        MultipleAssetMovementRequest multipleAssetMovementRequest = new MultipleAssetMovementRequest(new ArrayList<>());
        multipleAssetMovementRequest.getAssetMovementRequest().add(request1);
        multipleAssetMovementRequest.getAssetMovementRequest().add(request2);
        doNothing().when(assetMovementService).moveAssets(anyLong(), anyLong(), anyDouble());

        // Act
        assetMovementService.moveMultipleAssets(multipleAssetMovementRequest);

        // Assert
        verify(assetMovementService).moveAssets(request1.getSourceWalletId(), request1.getDestinationWalletId(), request1.getAmount());
        verify(assetMovementService).moveAssets(request2.getSourceWalletId(), request2.getDestinationWalletId(), request2.getAmount());
    }

    // Additional test cases can be added to cover other scenarios such as insufficient balance, exceptions, etc.
}
