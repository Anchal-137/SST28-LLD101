public class ClickStrategy implements OpenCloseStrategy {

    @Override
    public void start() {
        System.out.println("  Clicking pen to extend...");
    }

    @Override
    public void close() {
        System.out.println("  Clicking pen to retract.");
    }
}
