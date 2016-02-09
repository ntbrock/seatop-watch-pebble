package com.ntbrock.seatop.pebble.aggregate;

/**
 *
 * Stages of life are:
 *   New
 *   Midlife
 *   Aged
 *   Expired
 *
 * Created by brockman on 2/3/16.
 */
public class SeatopDataLifetime {

    public static final int SECONDS = 1;
    public static final int MINUTES = 60;
    public static final int HOURS = 3600;

    int newDuration;
    int agedDuration;
    int maxDuration;
    int unit;

    public SeatopDataLifetime(int newDuration, int agedDuration, int maxDuration, int unit) {
        this.newDuration = newDuration;
        this.agedDuration = agedDuration;
        this.maxDuration = maxDuration;
        this.unit = unit;
    }


    public boolean isPointNew(SeatopDataPoint point) {
        // keep it simple
        return point.ageInSeconds() < ( newDuration * unit );
    }

    public boolean isPointMidlife(SeatopDataPoint point) {
        // keep it simple
        if ( isPointNew(point) || point.ageInSeconds() >= ( agedDuration * unit ) ) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isPointAged(SeatopDataPoint point) {
        // Automatically short circuit to allow one and only one state at a time
        if (isPointExpired(point)) { return false; }
        return point.ageInSeconds() >= ( agedDuration * unit );
    }

    public boolean isPointExpired(SeatopDataPoint point) {
        // keep it simple
        return point.ageInSeconds() >= ( maxDuration * unit );
    }
}
