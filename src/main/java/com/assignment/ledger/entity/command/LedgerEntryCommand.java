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
@AllArgsConstructor
@Entity
@Table(name = "ledger_entry")
public class LedgerEntryCommand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "wallet_id")
    private WalletCommand wallet;

    private LocalDateTime timestamp;

    private double balance;

    // Movement state of the ledger entry
    @Enumerated(EnumType.STRING)
    private MovementState movementState;
}
