package wikilocation.pojos;

import wikilocation.utils.PositionCalculator;

/**
 * Created by swami on 09/04/14.
 */
public class Position {

    private Double lat;
    private Double lng;

    public Position(Double lat, Double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public Double getDistanceTo(Position otherPosition) {
        return PositionCalculator.distFrom(this.lat, this.lng, otherPosition.lat, otherPosition.lng);
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

}
