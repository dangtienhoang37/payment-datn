package com.springboot.app.controller;


import com.springboot.app.request.SubBalanceRequest;
import com.springboot.app.response.ApiResponse;
import com.springboot.app.service.PaymentTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment/user")
public class userPaymentController {

    @Autowired
    private PaymentTransactionService paymentTransactionService;
    // check wallet
    @GetMapping
    public ApiResponse getDetailWallet(@RequestHeader("Authorization") String token ) {
        return paymentTransactionService.getDetailUserWallet(token);
    }
    // internal request handler
    @PostMapping("/sub-balance")
    public ApiResponse internalSubBalance(@RequestBody SubBalanceRequest request){
        return paymentTransactionService.subBalance(request);

    }
}
