public class ClickMechanism implements PenMechanism {

    @Override
    public void open() {
        System.out.println("  Pressing click to deploy...");
    }

    @Override
    public void retract() {
        System.out.println("  Pressing click to retract.");
    }
}
