public class DriverAllocator implements DriverAllocatable {
    @Override
    public String allocate(String studentId) {
        // fake deterministic driver
        return "DRV-17";
    }
}
