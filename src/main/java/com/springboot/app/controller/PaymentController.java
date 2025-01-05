package com.springboot.app.controller;

import com.springboot.app.entity.PaymentStatus;
import com.springboot.app.entity.PaymentTransaction;
import com.springboot.app.entity.TransactionWallet;
import com.springboot.app.repository.PaymentTransactionRepository;
import com.springboot.app.repository.WalletRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import vn.payos.PayOS;
import vn.payos.type.Webhook;
import vn.payos.type.WebhookData;

@RestController
@RequestMapping("/payment")
public class PaymentController {
  private final PayOS payOS;
  private final PaymentTransactionRepository paymentTransactionRepository;
  private final WalletRepository walletRepository;

  public PaymentController(PayOS payOS, PaymentTransactionRepository paymentTransactionRepository,
                           WalletRepository walletRepository) {
    super();
    this.payOS = payOS;
    this.paymentTransactionRepository = paymentTransactionRepository;

    this.walletRepository = walletRepository;
  }

  @PostMapping(path = "/payos_transfer_handler")
  public ObjectNode payosTransferHandler(@RequestBody ObjectNode body)
      throws JsonProcessingException, IllegalArgumentException {

    ObjectMapper objectMapper = new ObjectMapper();
    ObjectNode response = objectMapper.createObjectNode();
    Webhook webhookBody = objectMapper.treeToValue(body, Webhook.class);

    try {
      // Init Response
      response.put("error", 0);
      response.put("message", "Webhook delivered");
      System.out.println("nhan data");

      response.set("data", null);

      WebhookData data = payOS.verifyPaymentWebhookData(webhookBody);
      // them logic query

      System.out.println(data.getOrderCode());
      // da nhan ordercode => set paymentstatus = 2 :paid => tang tien
      var targetTransaction  = paymentTransactionRepository.findByOrderCode(data.getOrderCode());
      targetTransaction.setPaymentStatus(PaymentStatus.SUCESS);
      paymentTransactionRepository.save(targetTransaction);
      // tang tien len thi phai co
      TransactionWallet targetWallet = targetTransaction.getTransactionWallet();
      targetWallet.setBalance(targetWallet.getBalance() + targetTransaction.getAmount());
      walletRepository.save(targetWallet);
      // done => tra response cho ui sang trang thanh cong va chuyen ve trang vi

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
