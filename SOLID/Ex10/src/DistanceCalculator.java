public class DistanceCalculator implements DistanceCalculable {
    @Override
    public double km(GeoPoint a, GeoPoint b) {
        // fake distance: rough Manhattan on scaled degrees for determinism
        double d = Math.abs(a.lat - b.lat) + Math.abs(a.lon - b.lon);
        double km = Math.round((d * 200.0) * 10.0) / 10.0;
        return km;
    }
}
