public class CapMechanism implements PenMechanism {

    @Override
    public void open() {
        System.out.println("  Taking off cap...");
    }

    @Override
    public void retract() {
        System.out.println("  Replacing cap.");
    }
}
