package com.bookmyshow.payment;

import com.bookmyshow.model.PaymentMethod;

/**
 * Factory Pattern: returns the correct PaymentGateway
 * based on the chosen payment method.
 */
public class PaymentGatewayFactory {

    public static PaymentGateway getGateway(PaymentMethod method) {
        switch (method) {
            case UPI:         return new UpiGateway();
            case CREDIT_CARD: return new CreditCardGateway();
            case DEBIT_CARD:  return new DebitCardGateway();
            default:
                throw new IllegalArgumentException("Unsupported payment method: " + method);
        }
    }
}
