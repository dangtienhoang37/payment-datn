package com.springboot.app.service;

import com.springboot.app.request.SubBalanceRequest;
import com.springboot.app.response.ApiResponse;

public interface PaymentTransactionService {
    ApiResponse getDetailUserWallet(String token);

    ApiResponse subBalance(SubBalanceRequest request);
}
