public class Main {
    public static void main(String[] args) {
        System.out.println("=== Cap Pen ===");
        Pen capPen = new Pen(new CapStrategy(), new Refill(Color.BLUE));
        capPen.write("Hello");

        capPen.changeRefill(new Refill(Color.RED));
        capPen.write("World");

        System.out.println("\n=== Click Pen ===");
        Pen clickPen = new Pen(new ClickStrategy(), new Refill(Color.BLACK));
        clickPen.write("Design Patterns");

        clickPen.changeRefill(new Refill(Color.GREEN));
        clickPen.write("are fun");
    }
}
