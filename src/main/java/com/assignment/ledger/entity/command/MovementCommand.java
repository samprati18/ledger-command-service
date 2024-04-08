package com.assignment.ledger.entity.command;

import com.assignment.ledger.entity.MovementState;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
//@AllArgsConstructor
@Entity
@Table(name = "movement")
public class MovementCommand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "from_wallet_id")
    private WalletCommand sourceWallet;

    @ManyToOne
    @JoinColumn(name = "to_wallet_id")
    private WalletCommand destinationWallet;

    private double amount;

    private String timestamp;

    // Movement state
    @Enumerated(EnumType.STRING)
    private MovementState state;
}
