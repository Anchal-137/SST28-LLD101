public class CapStrategy implements OpenCloseStrategy {

    @Override
    public void start() {
        System.out.println("  Removing cap...");
    }

    @Override
    public void close() {
        System.out.println("  Putting cap back on.");
    }
}
