package com.ntbrock.seatop.pebble.protocol;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 *
 * Must match pebble/src/seatop.h
 *
 * For conventience, I have left the java side in 'int'.
 * The single byte packing is handled by the serialization layer to make it easy for the rest of teh application.
 *
 * Created by brockman on 2/7/16.
 */
public class SeatopPointSummaryStruct {

    public static final int SIZEOF = 18;

    public int point_type; // enum POINT_TYPE_TWD

    public int now_number = 0;   // Latest Reading
    public int now_decimal = 0;

    public int max_number = 0;   // Maximum Reading w/ in Time Window.
    public int max_decimal = 0;

    public int min_number = 0;   // Minimum Reading w/in the time window.
    public int min_decimal = 0;

    // Full Averages - All the data within in the subcription window.
    public int full_avg_number = 0;
    public int full_avg_decimal = 0;
    public int full_avg_moving = 0;  // If we' have positive or negative motion in the average of all points

    // Half Averages - half of the points, the most recent ones.
    public int half_avg_number = 0;
    public int half_avg_decimal = 0;
    public int half_avg_moving = 0;  // If we' have positive or negative motion in the average


    public byte i2b ( int i ) {
        if ( i < 256 ) { return (byte) i; }
        return (byte) 0xFF;
    }

    public short i2s ( int i ) {
        return (short) i;
    }

    public ByteBuffer toByteBuffer() {
        ByteBuffer bb = ByteBuffer.allocate(SIZEOF);
        bb.order(ByteOrder.LITTLE_ENDIAN);

        bb.put(i2b(point_type));
        bb.putShort(i2s(now_number));
        bb.put(i2b(now_decimal));
        bb.putShort(i2s(max_number));
        bb.put(i2b(max_decimal));
        bb.putShort(i2s(min_number));
        bb.put(i2b(min_decimal));

        bb.putShort(i2s(full_avg_number));
        bb.put(i2b(full_avg_decimal));
        bb.put(i2b(full_avg_moving));
        bb.putShort(i2s(half_avg_number));
        bb.put(i2b(half_avg_decimal));
        bb.put(i2b(half_avg_moving));

        return bb;
    }

}

