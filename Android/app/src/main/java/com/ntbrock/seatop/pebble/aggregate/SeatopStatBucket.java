package com.ntbrock.seatop.pebble.aggregate;

/**
 * Created by brockman on 2/7/16.
 */
public class SeatopStatBucket {
    double minValue = 0;
    double maxValue = 0;
    int numTotal = 0;
    int numNew = 0;
    int numMidlife = 0;
    int numAged = 0;

    public void zero() {
        numTotal = 0;
        numNew = 0;
        numMidlife = 0;
        numAged = 0;
    }
}
