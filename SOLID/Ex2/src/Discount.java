import java.util.List;

public interface Discount {
    double discount(String customerType, double subtotal, List<OrderLine> lines);
}
