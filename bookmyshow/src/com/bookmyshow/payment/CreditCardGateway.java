package com.bookmyshow.payment;

import com.bookmyshow.model.Payment;
import com.bookmyshow.model.PaymentStatus;

public class CreditCardGateway implements PaymentGateway {

    @Override
    public boolean processPayment(Payment payment) {
        System.out.println("  [CREDIT_CARD] Processing payment of Rs." + payment.getAmount());
        payment.setStatus(PaymentStatus.SUCCESS);
        System.out.println("  [CREDIT_CARD] Payment successful: " + payment.getPaymentId());
        return true;
    }

    @Override
    public boolean processRefund(Payment payment) {
        System.out.println("  [CREDIT_CARD] Refunding Rs." + payment.getAmount() + " to credit card");
        payment.setStatus(PaymentStatus.REFUNDED);
        System.out.println("  [CREDIT_CARD] Refund successful: " + payment.getPaymentId());
        return true;
    }
}
