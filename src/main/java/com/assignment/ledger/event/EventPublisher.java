package com.assignment.ledger.event;

import com.assignment.ledger.entity.command.MovementCommand;
import com.assignment.ledger.entity.command.WalletCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class EventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    public EventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Async
    public CompletableFuture<Void> publishBalanceChangeEvent(WalletCommand wallet) {
        System.out.println("Thread Name1 " + Thread.currentThread().getName());
        BalanceChangeEvent event = new BalanceChangeEvent(this, wallet);
        applicationEventPublisher.publishEvent(event);
        return CompletableFuture.completedFuture(null);
    }

    @Async
    public CompletableFuture<Void> publishMovementEvent(MovementCommand movement) {
        System.out.println("Thread Name2 " + Thread.currentThread().getName());
        MovementEvent event = new MovementEvent(this, movement);
        applicationEventPublisher.publishEvent(event);
        return CompletableFuture.completedFuture(null);
    }
}
