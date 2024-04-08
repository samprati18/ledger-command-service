package com.assignment.ledger.entity.command;

import com.assignment.ledger.entity.AccountState;
import com.assignment.ledger.util.AccountNumberGenerator;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
@Entity
@Table(name = "account")
public class AccountCommand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_number", unique = true)
    private String accountNumber;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "entity_id")
    private EntityCommand entity;

    private String name;

    @Enumerated(EnumType.STRING)
    private AccountState state;

    public AccountCommand() {
        this.accountNumber = AccountNumberGenerator.generateAccountNumber();
    }

    @Override
    public String toString() {
        return "AccountCommand{" +
                "id=" + id +
                ", name='" + name + '\'' +
                // Exclude 'entity' field from toString()
                '}';
    }

}
