package com.springboot.app.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "transaction_wallet")
public class TransactionWallet {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;
  int balance;
  @Column(nullable = false)
  UUID accountId;


}