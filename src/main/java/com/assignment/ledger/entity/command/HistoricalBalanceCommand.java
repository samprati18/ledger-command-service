package com.assignment.ledger.entity.command;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Getter
@Setter
@Entity
@Table(name = "historical_balance")
public class HistoricalBalanceCommand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //@JsonBackReference
    @ManyToOne
    @JoinColumn(name = "wallet_id")
    private WalletCommand wallet;

    private double balance;

    private String timestamp;


}
