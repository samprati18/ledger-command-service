package com.assignment.ledger.entity.command;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@Entity
@Table(name = "wallet")
public class WalletCommand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;


    @ManyToOne
    @JoinColumn(name = "account_id")
    private AccountCommand account;

    @ManyToOne
    @JoinColumn(name = "asset_id")
    private AssetCommand asset;

    private double balance;

    @OneToMany(mappedBy = "wallet", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<HistoricalBalanceCommand> historicalBalances;

    public WalletCommand() {
    }


}
