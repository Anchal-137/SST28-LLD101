public class AttendanceScanner implements PowerControllable, AttendanceScannable {

    @Override public void powerOn() { /* ok */ }
    @Override public void powerOff() { /* no output */ }

    @Override public int scanAttendance() { return 3; }
}
