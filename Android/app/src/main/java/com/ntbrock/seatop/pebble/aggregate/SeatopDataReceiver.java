package com.ntbrock.seatop.pebble.aggregate;

/**
 * Created by brockman on 2/3/16.
 */
public interface SeatopDataReceiver {

    public void handleSinglePoint(SeatopDataPoint dataPoint);

}

