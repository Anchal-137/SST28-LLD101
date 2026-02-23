import java.util.*;
public class Invoice {
    private final String id;
    private final String customerType;
    private final List<OrderLine> lines;
    
    public Invoice(String id, String customerType, List<OrderLine> lines) {
        this.id = id;
        this.customerType = customerType;
        this.lines = lines;
    }
    
    public String getId() { return id; }
    public String getCustomerType() { return customerType; }
    public List<OrderLine> getLines() { return lines; }
}
