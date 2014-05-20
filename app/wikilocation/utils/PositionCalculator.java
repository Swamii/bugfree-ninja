package wikilocation.utils;

import wikilocation.pojos.Position;

/**
 * Created by swami on 09/04/14.
 */
public class PositionCalculator {

    /**
     * Calculates distance in meters between two coordinates
     * @param lat1
     * @param lng1
     * @param lat2
     * @param lng2
     * @return
     */
    public static Double distFrom(Double lat1, Double lng1, Double lat2, Double lng2) {
        double earthRadius = 3958.75;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                         Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double dist = earthRadius * c;

        int meterConversion = 1609;

        return (Double) (dist * meterConversion);
    }

    /**
     * Calculate offset in meters from one point to another.
     * @param lat
     * @param lng
     * @param offset
     * @return
     */
    public static Position calculateOffset(Double lat, Double lng, Integer offset) {
        Double earthRadius = 6378137D;

        //Coordinate offsets in radians
        Double dLat = offset / earthRadius;
        Double dLng = offset / (earthRadius * Math.cos(Math.PI * lat / 180));

        //OffsetPosition, decimal degrees
        lat = lat + dLat * 180 / Math.PI;
        lng = lng + dLng * 180 / Math.PI;

        return new Position(lat, lng);
    }

    public static Double distFrom(Position pos1, Position pos2) {
        return distFrom(pos1.getLat(), pos1.getLng(), pos2.getLat(), pos2.getLng());
    }

    public static Position[] getSquare(Double lat, Double lng, Integer offset) {
        Position offset1 = calculateOffset(lat, lng, offset);
        Position offset2 = calculateOffset(lat, lng, -offset);
        return new Position[] {offset1, offset2};
    }

    public static Position[] getSquare(Position pos, Integer offset) {
        return getSquare(pos.getLat(), pos.getLng(), offset);
    }

}
