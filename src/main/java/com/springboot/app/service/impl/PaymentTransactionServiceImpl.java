package com.springboot.app.service.impl;

import com.springboot.app.entity.PaymentStatus;
import com.springboot.app.entity.PaymentTransaction;
import com.springboot.app.repository.PaymentTransactionRepository;
import com.springboot.app.repository.WalletRepository;
import com.springboot.app.request.SubBalanceRequest;
import com.springboot.app.response.ApiResponse;
import com.springboot.app.service.PaymentTransactionService;
import com.springboot.app.utils.JwToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

@Service
public class PaymentTransactionServiceImpl implements PaymentTransactionService {
    @Autowired
    private JwToken jwToken;
    @Autowired
    private WalletRepository walletRepository;
    @Autowired
    private PaymentTransactionRepository paymentTransactionRepository;
    @Override
    public ApiResponse getDetailUserWallet(String token) {

        UUID AccountId = jwToken.getIdFromToken(token);
        // sau doan nay call base service. lay ra Account OBJ -> targetwallet
        var targetWallet = walletRepository.findByAccountId(AccountId).orElse(null);
        if(Objects.isNull(targetWallet)){
            throw new RuntimeException("cant find wallet");
        }
        ApiResponse response= ApiResponse.builder()
                .code(1000)
                .message("get Detail wallet sucessfully")
                .isSucess(true)
                .data(targetWallet)
                .build();
        return response;
    }

    @Override
    public ApiResponse subBalance(SubBalanceRequest request) {
        UUID accountId = request.getAccountId();
        var outstandingBalance = request.getBalance();
        var targetWallet = walletRepository.findByAccountId(accountId).orElse(null);
        if(Objects.isNull(targetWallet)){
            throw new RuntimeException("cant find wallet");
        }
        if(targetWallet.getBalance() < outstandingBalance){
            throw new RuntimeException("Số dư không đủ, vui lòng nạp thêm");
        }
        targetWallet.setBalance(targetWallet.getBalance() - outstandingBalance);
        new PaymentTransaction();
        paymentTransactionRepository.save(PaymentTransaction.builder()
                        .transactionWallet(targetWallet)
                        .paymentStatus(PaymentStatus.SUCESS)
                        .balance(targetWallet.getBalance())
                        .amount(-outstandingBalance)
                        .orderCode(-1)
                .build());
        ApiResponse response= ApiResponse.builder()
                .code(1000)
                .message(" sub transaction  sucessfully")
                .isSucess(true)
                .data(walletRepository.save(targetWallet))
                .build();
        return response;
    }

    @Override
    public ApiResponse getHistory(String token) {
        var accountId = jwToken.getIdFromToken(token);
        var targetWallet = walletRepository.findByAccountId(accountId).orElse(null);
        if(Objects.isNull(targetWallet)){
            throw new RuntimeException("cant find wallet");
        }
        var targetHistory = paymentTransactionRepository.findByTransactionWallet(targetWallet);

        ApiResponse response= ApiResponse.builder()
                .code(1000)
                .message("history")
                .isSucess(true)
                .data(targetHistory)
                .build();
        return response;
    }
}
