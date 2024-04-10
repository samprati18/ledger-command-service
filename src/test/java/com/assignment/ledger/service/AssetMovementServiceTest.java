package com.assignment.ledger.service;

import com.assignment.ledger.dto.AssetMovementRequest;
import com.assignment.ledger.dto.MultipleAssetMovementRequest;
import com.assignment.ledger.entity.command.WalletCommand;
import com.assignment.ledger.event.EventPublisher;
import com.assignment.ledger.event.KafkaEventPublisher;
import com.assignment.ledger.exception.WalletNotFoundException;
import com.assignment.ledger.mapper.EntityMapper;
import com.assignment.ledger.repository.HistoricalBalanceCommandRepository;
import com.assignment.ledger.repository.MovementCommandRepository;
import com.assignment.ledger.repository.WalletCommandRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


import static org.mockito.Mockito.*;


public class AssetMovementServiceTest {

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

    @Mock
    private EntityMapper entityMapper;

    @InjectMocks
    private AssetMovementService assetMovementService;


    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(assetMovementService, "movementCommandTopic", "ledger-asset-movementCommand-event");
        ReflectionTestUtils.setField(assetMovementService, "walletEventTopic", "ledger-account-wallet-event");
        ReflectionTestUtils.setField(assetMovementService, "historicalBalanceEventTopic", "ledger-historical-balance-event");
    }

    @Test
    public void testMoveAssets_Success() {
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
        verify(kafkaEventPublisher, times(5)).publishCommandEvents(anyString(), any());

    }

    @Test(expected = WalletNotFoundException.class)
    public void testMoveAssets_Failed() {
        double amount = 150.0; // Exceeds source wallet balance
        assetMovementService.moveAssets(1L, 2L, amount);
    }
}
