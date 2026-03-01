// Abstraction for payment processing
public interface PaymentProcessable {
    String charge(String studentId, double amount);
}
