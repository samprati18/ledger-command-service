package com.assignment.ledger.repository;

import com.assignment.ledger.entity.command.WalletCommand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletCommandRepository extends JpaRepository<WalletCommand, Long> {
}
