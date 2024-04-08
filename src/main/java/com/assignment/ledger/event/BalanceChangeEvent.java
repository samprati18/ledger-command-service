package com.assignment.ledger.event;

import com.assignment.ledger.entity.command.WalletCommand;
import org.springframework.context.ApplicationEvent;

public class BalanceChangeEvent extends ApplicationEvent {

    private final WalletCommand wallet;

    public BalanceChangeEvent(Object source, WalletCommand wallet) {
        super(source);
        this.wallet = wallet;
    }

    public WalletCommand getWallet() {
        return wallet;
    }
}
