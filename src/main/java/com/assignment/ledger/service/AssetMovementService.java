package com.assignment.ledger.service;

import com.assignment.ledger.dto.AssetMovementRequest;
import com.assignment.ledger.dto.MultipleAssetMovementRequest;
import com.assignment.ledger.entity.MovementState;
import com.assignment.ledger.entity.command.HistoricalBalanceCommand;
import com.assignment.ledger.entity.command.MovementCommand;
import com.assignment.ledger.entity.command.WalletCommand;
import com.assignment.ledger.event.EventPublisher;
import com.assignment.ledger.event.KafkaEventPublisher;
import com.assignment.ledger.exception.AssetMovementFailedException;
import com.assignment.ledger.exception.WalletNotFoundException;
import com.assignment.ledger.mapper.EntityMapper;
import com.assignment.ledger.repository.HistoricalBalanceCommandRepository;
import com.assignment.ledger.repository.MovementCommandRepository;
import com.assignment.ledger.repository.WalletCommandRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * The AssetMovementService handles the movement of assets between wallets and related operations.
 */
@Service
public class AssetMovementService {
    // Repositories for accessing data from the database
    private final MovementCommandRepository movementRepository;
    private final WalletCommandRepository walletRepository;
    private final HistoricalBalanceCommandRepository historicalBalanceRepository;

    // Event publishers for broadcasting balance changes and movements
    private final EventPublisher eventPublisher;
    private final KafkaEventPublisher kafkaEventPublisher;

    // Mapper for mapping entity objects to DTOs
    private final EntityMapper entityMapper;

    /**
     * Constructs an instance of AssetMovementService with the necessary dependencies.
     *
     * @param movementRepository          The repository for movement commands
     * @param walletRepository            The repository for wallet commands
     * @param historicalBalanceRepository The repository for historical balance commands
     * @param eventPublisher              The event publisher for broadcasting events
     * @param kafkaEventPublisher         The Kafka event publisher for asynchronous event broadcasting
     * @param entityMapper                The mapper for entity-to-DTO mapping
     */
    @Autowired
    public AssetMovementService(MovementCommandRepository movementRepository, WalletCommandRepository walletRepository, HistoricalBalanceCommandRepository historicalBalanceRepository, EventPublisher eventPublisher, KafkaEventPublisher kafkaEventPublisher, EntityMapper entityMapper) {
        this.movementRepository = movementRepository;
        this.walletRepository = walletRepository;
        this.historicalBalanceRepository = historicalBalanceRepository;
        this.eventPublisher = eventPublisher;
        this.kafkaEventPublisher = kafkaEventPublisher;
        this.entityMapper = entityMapper;
    }

    /**
     * Moves multiple assets according to the provided request.
     *
     * @param request The request containing multiple asset movement details
     */
    @Transactional
    public void moveMultipleAssets(MultipleAssetMovementRequest request) {
        List<AssetMovementRequest> moves = request.getAssetMovementRequest();
        moves.parallelStream().forEach(move -> moveAssets(move.getSourceWalletId(), move.getDestinationWalletId(), move.getAmount()));
    }

    /**
     * Moves assets from the source wallet to the destination wallet.
     *
     * @param sourceWalletId      The ID of the source wallet
     * @param destinationWalletId The ID of the destination wallet
     * @param amount              The amount of assets to be moved
     */
    @Transactional
    public void moveAssets(Long sourceWalletId, Long destinationWalletId, double amount) {
        // Retrieve source and destination wallets from the repository
        WalletCommand sourceWallet = walletRepository.findById(sourceWalletId)
                .orElseThrow(() -> new WalletNotFoundException("Source wallet not found"));
        WalletCommand destinationWallet = walletRepository.findById(destinationWalletId)
                .orElseThrow(() -> new WalletNotFoundException("Destination wallet not found"));
        // Determine the state of the movement (Cleared, Pending, or Failed)
        MovementState state = determineMovementState(sourceWallet, destinationWallet, amount);
        if (state == MovementState.CLEARED) {
            // If the movement is cleared, update the wallets, record historical balances, and publish events
            MovementCommand movementCommand = new MovementCommand();
            movementCommand.setSourceWallet(sourceWallet);
            movementCommand.setDestinationWallet(destinationWallet);
            movementCommand.setAmount(amount);
            movementCommand.setTimestamp(String.valueOf(LocalDateTime.now()));
            movementCommand.setState(MovementState.CLEARED);
            movementRepository.save(movementCommand);
            // Record historical balances
            recordHistoricalBalances(sourceWallet, destinationWallet);
            // Update wallet balances
            updateWalletBalance(sourceWallet, destinationWallet, amount);
            // Broadcast balance changes
            eventPublisher.publishBalanceChangeEvent(sourceWallet);
            eventPublisher.publishBalanceChangeEvent(destinationWallet);
            // Broadcast movementCommand
            eventPublisher.publishMovementEvent(movementCommand);
            //sync query
            kafkaEventPublisher.publishCommandEvents("ledger-asset-movementCommand-event", entityMapper.toDTO(movementCommand));
            kafkaEventPublisher.publishCommandEvents("ledger-account-wallet-event", entityMapper.toDTO(sourceWallet));
            kafkaEventPublisher.publishCommandEvents("ledger-account-wallet-event", entityMapper.toDTO(destinationWallet));
        } else if (state == MovementState.FAILED) {
            // If the movement failed, throw an exception
            MovementCommand movement = new MovementCommand();
            movement.setSourceWallet(sourceWallet);
            movement.setDestinationWallet(destinationWallet);
            movement.setAmount(amount);
            movement.setTimestamp(String.valueOf(LocalDateTime.now()));
            movement.setState(MovementState.FAILED);
            movementRepository.save(movement);
            throw new AssetMovementFailedException("Movement failed due to insufficient balance or other error");
        } else {
            // If the movement is pending, save the movement command with the PENDING state
            MovementCommand movement = new MovementCommand();
            movement.setSourceWallet(sourceWallet);
            movement.setDestinationWallet(destinationWallet);
            movement.setAmount(amount);
            movement.setTimestamp(String.valueOf(LocalDateTime.now()));
            movement.setState(MovementState.PENDING);
            movementRepository.save(movement);
        }
    }

    private MovementState determineMovementState(WalletCommand sourceWallet, WalletCommand destinationWallet, double amount) {
        try {
            // Check if the source wallet has sufficient balance
            if (sourceWallet.getBalance() < amount) {
                return MovementState.FAILED;
            }
            // Deduct amount from source wallet and add it to destination wallet
            sourceWallet.setBalance(sourceWallet.getBalance() - amount);
            destinationWallet.setBalance(destinationWallet.getBalance() + amount);
            // If the movement completes successfully without exceptions, set state to COMPLETED
            return MovementState.CLEARED;
        } catch (Exception e) {
            // Log the error or handle it appropriately
            return MovementState.FAILED;
        }
    }

    // Method to record historical balances
    private void recordHistoricalBalances(WalletCommand sourceWallet, WalletCommand destinationWallet) {
        HistoricalBalanceCommand historicalBalanceSource = new HistoricalBalanceCommand();
        historicalBalanceSource.setWallet(sourceWallet);
        historicalBalanceSource.setBalance(sourceWallet.getBalance());
        historicalBalanceSource.setTimestamp(String.valueOf(LocalDateTime.now()));
        historicalBalanceRepository.save(historicalBalanceSource);
        HistoricalBalanceCommand historicalBalanceDestination = new HistoricalBalanceCommand();
        historicalBalanceDestination.setWallet(destinationWallet);
        historicalBalanceDestination.setBalance(destinationWallet.getBalance());
        historicalBalanceDestination.setTimestamp(String.valueOf(LocalDateTime.now()));
        historicalBalanceRepository.save(historicalBalanceDestination);
        kafkaEventPublisher.publishCommandEvents("ledger-historical-balance-event", entityMapper.toDTO(historicalBalanceSource));
        kafkaEventPublisher.publishCommandEvents("ledger-historical-balance-event", entityMapper.toDTO(historicalBalanceDestination));
    }

    // Method to update wallet balances
    private void updateWalletBalance(WalletCommand sourceWallet, WalletCommand destinationWallet, double amount) {
        sourceWallet.setBalance(sourceWallet.getBalance() - amount);
        destinationWallet.setBalance(destinationWallet.getBalance() + amount);
        walletRepository.save(sourceWallet);
        walletRepository.save(destinationWallet);
    }

}
