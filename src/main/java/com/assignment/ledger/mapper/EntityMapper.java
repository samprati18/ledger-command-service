package com.assignment.ledger.mapper;

import com.assignment.ledger.dto.AccountCommandDTO;
import com.assignment.ledger.dto.HistoricalBalanceCommandDTO;
import com.assignment.ledger.dto.MovementCommandDTO;
import com.assignment.ledger.dto.WalletCommandDTO;
import com.assignment.ledger.entity.command.AccountCommand;
import com.assignment.ledger.entity.command.HistoricalBalanceCommand;
import com.assignment.ledger.entity.command.MovementCommand;
import com.assignment.ledger.entity.command.WalletCommand;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EntityMapper {
    private final ModelMapper modelMapper;

    @Autowired
    public EntityMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public AccountCommandDTO toDTO(AccountCommand accountCommand) {
        return modelMapper.map(accountCommand, AccountCommandDTO.class);
    }

    public MovementCommandDTO toDTO(MovementCommand movementCommand) {
        return modelMapper.map(movementCommand, MovementCommandDTO.class);
    }

    public WalletCommandDTO toDTO(WalletCommand walletCommand) {
        return modelMapper.map(walletCommand, WalletCommandDTO.class);
    }

    public HistoricalBalanceCommandDTO toDTO(HistoricalBalanceCommand historicalBalanceCommand) {
        return modelMapper.map(historicalBalanceCommand, HistoricalBalanceCommandDTO.class);
    }


}
