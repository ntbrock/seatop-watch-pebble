package com.ntbrock.seatop.pebble.aggregate;

import com.ntbrock.seatop.pebble.protocol.SeatopPebbleProtocol;

/**
 * Created by brockman on 2/3/16.
 */
public class SeatopDataPoint {

    public Long timestamp;
    public Double timevalue;
    public SeatopMeasurementType measurement;

    public String toString() {
        // Could make this prettier now.
        return timevalue + " " + measurement.shortLabel + "." + measurement.units + " (Age: " + ageInSeconds() + " sec)";
    }

    public SeatopDataPoint( Long timestamp, Double timevalue, SeatopMeasurementType measurement) {

        this.timestamp = timestamp;
        this.timevalue = timevalue;
        this.measurement = measurement;
    }

    public SeatopDataPoint( Double timevalue, SeatopMeasurementType measurement) {

        this.timestamp = System.currentTimeMillis();
        this.timevalue = timevalue;
        this.measurement = measurement;
    }

    public int ageInSeconds() {
        return (int)(( System.currentTimeMillis() - this.timestamp ) / 1000);
    }


    /**
     * Conventence routine to generate time of day in seconds, UTC Z
     * @return
     */
    public static SeatopDataPoint newTimeOfDaySeconds () {
        return new SeatopDataPoint(
                System.currentTimeMillis(),
                (double) ((System.currentTimeMillis() / 1000) % 86400),
                SeatopMeasurementType.M_TOD
        );
    }


}
