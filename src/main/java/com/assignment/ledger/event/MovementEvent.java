package com.assignment.ledger.event;

import com.assignment.ledger.entity.command.MovementCommand;
import org.springframework.context.ApplicationEvent;

public class MovementEvent extends ApplicationEvent {

    private final MovementCommand movement;

    public MovementEvent(Object source, MovementCommand movement) {
        super(source);
        this.movement = movement;
    }

    public MovementCommand getMovement() {
        return movement;
    }
}
