package com.bookmyshow.payment;

import com.bookmyshow.model.Payment;
import com.bookmyshow.model.PaymentStatus;

public class UpiGateway implements PaymentGateway {

    @Override
    public boolean processPayment(Payment payment) {
        System.out.println("  [UPI] Processing payment of Rs." + payment.getAmount());
        // Simulate UPI payment success
        payment.setStatus(PaymentStatus.SUCCESS);
        System.out.println("  [UPI] Payment successful: " + payment.getPaymentId());
        return true;
    }

    @Override
    public boolean processRefund(Payment payment) {
        System.out.println("  [UPI] Refunding Rs." + payment.getAmount() + " to UPI account");
        payment.setStatus(PaymentStatus.REFUNDED);
        System.out.println("  [UPI] Refund successful: " + payment.getPaymentId());
        return true;
    }
}
