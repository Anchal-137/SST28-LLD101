// This fat interface has been replaced by capability-specific interfaces:
// PowerControllable, BrightnessControllable, TemperatureControllable,
// AttendanceScannable, InputConnectable.
//
// Kept as a marker to show the refactoring path — no device implements this anymore.

public interface SmartClassroomDevice extends
        PowerControllable, BrightnessControllable, TemperatureControllable,
        AttendanceScannable, InputConnectable {
    // empty — this aggregates all capabilities for backward compat if ever needed
}
