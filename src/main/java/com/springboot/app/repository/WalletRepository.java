package com.springboot.app.repository;

import com.springboot.app.entity.TransactionWallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface WalletRepository extends JpaRepository<TransactionWallet, UUID> {
    Optional<TransactionWallet> findByAccountId(UUID accountId);
}