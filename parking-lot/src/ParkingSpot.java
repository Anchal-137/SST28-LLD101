public class ParkingSpot {
    private final int spotNumber;
    private final SlotType type;
    private final int floor;
    private boolean reserved;

    public ParkingSpot(int spotNumber, SlotType type, int floor) {
        this.spotNumber = spotNumber;
        this.type = type;
        this.floor = floor;
        this.reserved = false;
    }

    public int getSpotNumber() { return spotNumber; }
    public SlotType getType() { return type; }
    public int getFloor() { return floor; }
    public boolean isReserved() { return reserved; }

    public void reserve() { this.reserved = true; }
    public void release() { this.reserved = false; }
}
