public class Pen {
    private final PenMechanism mechanism;
    private Refill cartridge;

    public Pen(PenMechanism mechanism, Refill cartridge) {
        this.mechanism = mechanism;
        this.cartridge = cartridge;
    }

    public void write(String text) {
        mechanism.open();
        System.out.println("  Writing '" + text + "' in " + cartridge.getInkColor());
        mechanism.retract();
    }

    public void swapRefill(Refill newCartridge) {
        System.out.println("  Swapping refill: " + cartridge.getInkColor() + " -> " + newCartridge.getInkColor());
        this.cartridge = newCartridge;
    }

    public InkColor getInkColor() { return cartridge.getInkColor(); }
}
