package com.assignment.ledger.repository;

import com.assignment.ledger.entity.command.HistoricalBalanceCommand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistoricalBalanceCommandRepository extends JpaRepository<HistoricalBalanceCommand, Long> {
}
