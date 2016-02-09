package com.ntbrock.seatop.pebble.aggregate;

/**
 * Created by brockman on 2/3/16.
 */
public class SeatopDataFilter {

    public SeatopMeasurementType measurement;

    public SeatopDataFilter(SeatopMeasurementType measurement) {

        this.measurement = measurement;
    }

    public boolean isPointInteresting( SeatopDataPoint point ) {
        return point.measurement.equals(this.measurement);
    }

    public String toString() {
        return "include("+this.measurement.shortLabel+")";
    }
}
