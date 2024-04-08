package com.assignment.ledger.repository;

import com.assignment.ledger.entity.command.AccountCommand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository  extends JpaRepository<AccountCommand,Long> {
    Optional<AccountCommand> findByAccountNumber(String accountNumber);
}
