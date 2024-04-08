package com.assignment.ledger.repository;

import com.assignment.ledger.entity.command.MovementCommand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovementCommandRepository extends JpaRepository<MovementCommand, Long> {
}
