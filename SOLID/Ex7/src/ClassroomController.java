public class ClassroomController {
    private final DeviceRegistry reg;

    public ClassroomController(DeviceRegistry reg) { this.reg = reg; }

    public void startClass() {
        // projector: needs power + input
        Projector pj = (Projector) reg.getFirstOfType("Projector");
        pj.powerOn();
        pj.connectInput("HDMI-1");

        // lights: needs brightness
        BrightnessControllable lights = reg.getFirst(BrightnessControllable.class);
        lights.setBrightness(60);

        // AC: needs temperature
        TemperatureControllable ac = reg.getFirst(TemperatureControllable.class);
        ac.setTemperatureC(24);

        // scanner: needs attendance scanning
        AttendanceScannable scanner = reg.getFirst(AttendanceScannable.class);
        System.out.println("Attendance scanned: present=" + scanner.scanAttendance());
    }

    public void endClass() {
        System.out.println("Shutdown sequence:");
        // shut down all devices that support power control, in the order
        // they were registered — but only certain ones produce output
        ((PowerControllable) reg.getFirstOfType("Projector")).powerOff();
        ((PowerControllable) reg.getFirstOfType("LightsPanel")).powerOff();
        ((PowerControllable) reg.getFirstOfType("AirConditioner")).powerOff();
    }
}
