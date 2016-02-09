package com.ntbrock.seatop.pebble.protocol;

/**
 * Created by brockman on 2/7/16.
 */
public class SeatopPointDistroStruct {

    int point_type;

    // Picked 20 arbirarility.  This is a limitation of the protocol
    // Will not allow of very large, dyanmically sized histograms.
    // It also means that smaller historgrams will transmit empty data.
    SeatopDistroBucketStruct bucket[] = new SeatopDistroBucketStruct[20];

    // seatop_distro_bucket bucket[20]; // I can do pointer math here, make iteration easier and less verbose.

    int start_bucket_number; // Used for rendering with a subset of information
    int stop_bucket_number;

    int youngest_bucket_number;
    int oldest_bucket_number;
}
