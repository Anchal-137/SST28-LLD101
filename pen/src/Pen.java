public class Pen {
    private final OpenCloseStrategy openClose;
    private Refill refill;

    public Pen(OpenCloseStrategy openClose, Refill refill) {
        this.openClose = openClose;
        this.refill = refill;
    }

    public void write(String text) {
        openClose.start();
        System.out.println("  Writing '" + text + "' in " + refill.getColor());
        openClose.close();
    }

    public void changeRefill(Refill newRefill) {
        System.out.println("  Changing refill: " + refill.getColor() + " -> " + newRefill.getColor());
        this.refill = newRefill;
    }

    public Color getColor() { return refill.getColor(); }
}
