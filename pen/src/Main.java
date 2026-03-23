public class Main {
    public static void main(String[] args) {
        System.out.println("=== Cap Pen ===");
        Pen capPen = new Pen(new CapMechanism(), new Refill(InkColor.BLUE));
        capPen.write("Hello");

        capPen.swapRefill(new Refill(InkColor.RED));
        capPen.write("World");

        System.out.println("\n=== Click Pen ===");
        Pen clickPen = new Pen(new ClickMechanism(), new Refill(InkColor.BLACK));
        clickPen.write("Design Patterns");

        clickPen.swapRefill(new Refill(InkColor.GREEN));
        clickPen.write("are fun");
    }
}
