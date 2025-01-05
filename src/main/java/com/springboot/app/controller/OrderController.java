package com.springboot.app.controller;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

import com.springboot.app.entity.PaymentStatus;
import com.springboot.app.entity.PaymentTransaction;
import com.springboot.app.entity.TransactionWallet;
import com.springboot.app.repository.PaymentTransactionRepository;
import com.springboot.app.repository.WalletRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.springboot.app.type.CreatePaymentLinkRequestBody;

import vn.payos.PayOS;
import vn.payos.type.CheckoutResponseData;
import vn.payos.type.ItemData;
import vn.payos.type.PaymentData;
import vn.payos.type.PaymentLinkData;

import static com.springboot.app.entity.PaymentStatus.PENDING;

@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {
    private final PayOS payOS;
    @Autowired
    private PaymentTransactionRepository paymentTransactionRepository;
    @Autowired
    private WalletRepository walletRepository;

    public OrderController(PayOS payOS, PaymentTransactionRepository paymentTransactionRepository, WalletRepository walletRepository) {
        super();
        this.payOS = payOS;
        this.paymentTransactionRepository = paymentTransactionRepository;
        this.walletRepository = walletRepository;
    }

    @PostMapping(path = "/create")
    public ObjectNode createPaymentLink( @RequestBody CreatePaymentLinkRequestBody RequestBody) {
        log.info(RequestBody.getReturnUrl());
        log.info(RequestBody.getCancelUrl());
        log.info(RequestBody.getDescription());
        log.info(RequestBody.getProductName());
        log.info(String.valueOf(RequestBody.getPrice()));
        // lấy userID -> tìm user-> lấy ra wallet

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode response = objectMapper.createObjectNode();
        try {
            final String productName = RequestBody.getProductName();
            final String description = RequestBody.getDescription();
            final String returnUrl = RequestBody.getReturnUrl();
            final String cancelUrl = RequestBody.getCancelUrl();
            final int price = RequestBody.getPrice();
            var wallet = walletRepository.findByAccountId(RequestBody.getAccountId()).orElse(null);
            if(Objects.isNull(wallet)){
                throw new RuntimeException("cant find account");
            }
            // Gen order code
            String currentTimeString = String.valueOf(String.valueOf(new Date().getTime()));
            // orderCode
            long orderCode = Long.parseLong(currentTimeString.substring(currentTimeString.length() - 6));

            ItemData item = ItemData.builder().name(productName).price(price).quantity(1).build();

            PaymentData paymentData = PaymentData.builder()
                    .orderCode(orderCode)
                    .description(description)
                    .amount(price)
                    .item(item)
                    .returnUrl(returnUrl)
                    .cancelUrl(cancelUrl)
                    .build();







            CheckoutResponseData data = payOS.createPaymentLink(paymentData);
            new PaymentTransaction();

            // build data xong. => tạo record payment transaction để theo dõi trong db

            PaymentTransaction newTransaction = PaymentTransaction.builder()
                    .amount(data.getAmount())
                    .paymentStatus(PaymentStatus.valueOf(data.getStatus()))
                    .orderCode(data.getOrderCode())
                    .transactionWallet(wallet)
                    .build();
            paymentTransactionRepository.save(newTransaction);

            response.put("error", 0);
            response.put("message", "success");
            response.set("data", objectMapper.valueToTree(data));
            return response;

        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", -1);
            response.put("message", "fail");
            response.set("data", null);
            return response;

        }
    }

    @GetMapping(path = "/{orderId}")
    public ObjectNode getOrderById(@PathVariable("orderId") long orderId) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode response = objectMapper.createObjectNode();

        try {
            PaymentLinkData order = payOS.getPaymentLinkInformation(orderId);

            response.set("data", objectMapper.valueToTree(order));
            response.put("error", 0);
            response.put("message", "ok");
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", -1);
            response.put("message", e.getMessage());
            response.set("data", null);
            return response;
        }

    }

    @PutMapping(path = "/{orderId}")
    public ObjectNode cancelOrder(@PathVariable("orderId") int orderId) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode response = objectMapper.createObjectNode();
        try {
            PaymentLinkData order = payOS.cancelPaymentLink(orderId, null);
            response.set("data", objectMapper.valueToTree(order));
            response.put("error", 0);
            response.put("message", "ok");
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", -1);
            response.put("message", e.getMessage());
            response.set("data", null);
            return response;
        }
    }

    @PostMapping(path = "/confirm-webhook")
    public ObjectNode confirmWebhook(@RequestBody Map<String, String> requestBody) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode response = objectMapper.createObjectNode();
        try {
            String str = payOS.confirmWebhook(requestBody.get("webhookUrl"));
            System.out.println("test cfwh"+str);
            response.set("data", objectMapper.valueToTree(str));
            response.put("error", 0);
            response.put("message", "ok");
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", -1);
            response.put("message", e.getMessage());
            response.set("data", null);
            return response;
        }
    }
}
