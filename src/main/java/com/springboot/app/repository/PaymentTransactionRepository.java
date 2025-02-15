package com.springboot.app.repository;

import com.springboot.app.entity.TransactionWallet;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.springboot.app.entity.PaymentTransaction;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, UUID> {
    PaymentTransaction findByOrderCode(@NonNull Long orderCode);
    List<PaymentTransaction> findByTransactionWallet(TransactionWallet transactionWallet);

}
