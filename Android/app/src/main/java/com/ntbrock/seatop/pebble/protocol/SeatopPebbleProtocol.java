package com.ntbrock.seatop.pebble.protocol;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;
import com.ntbrock.seatop.pebble.aggregate.SeatopDataPoint;
import com.ntbrock.seatop.pebble.aggregate.SeatopFrequencyAggregator;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * When you have to implement a protocol it becomes so... permanent.
 * I have really enjoyed daydreaming, sketching, prototyping for the past 3
 * days, but if I don't do this step, it will never finally come to life.
 *
 *
 * Best reference so far:
 * /Users/brockman/Library/Application Support/Pebble SDK/SDKs/3.8.2/sdk-core/pebble/aplite/include
 *
 * Created by brockman on 2/6/16.
 */
public class SeatopPebbleProtocol {

    // From pebble build directory. appinfo.json
    public static final String PEBBLE_APPLICATION_UUID = "7db89cb2-8469-3266-960e-2498cdf4a306";
    // Use like so:  private static final UUID SEATOP_UUID = UUID.fromString(SeatopPebbleProtocol.PEBBLE_APPLICATION_UUID);

    // Channel 1 is basically the TOP of the watch, Channel 2 is the bottom of the watch.
    public static final SeatopPebbleChannel CHANNEL_1 = new SeatopPebbleChannel(SeatopPebbleChannel.CHANNEL_NUMBER_1);
    public static final SeatopPebbleChannel CHANNEL_2 = new SeatopPebbleChannel(SeatopPebbleChannel.CHANNEL_NUMBER_2);

    // This matches pebble/src/seatop.h enumerations
    public static final int ANDROID_TO_PEBBLE_INFO_MESSAGE = 100;       // Used for arbitrary text display of cstring
    public static final int ANDROID_TO_PEBBLE_POINT_SUMMARY_CH1 = 101;
    public static final int ANDROID_TO_PEBBLE_POINT_SUMMARY_CH2 = 102;

    public static final int ANDROID_TO_PEBBLE_POINT_DISTRO_CH1 = 111;
    public static final int ANDROID_TO_PEBBLE_POINT_DISTRO_CH2 = 112;

    public static final int ANDROID_TO_PEBBLE_ALARM_MESSAGE = 150;
    public static final int ANDROID_TO_PEBBLE_POINT_ALARM_CH1 = 151;
    public static final int ANDROID_TO_PEBBLE_POINT_ALARM_CH2 = 152;

    // For now, support two channels of information.
    public static final int PEBBLE_TO_ANDROID_SUBSCRIBE_SECONDS = 200;  // Pebble can choose how many seconds the aggregation runs over. applies to ALL channels.
    public static final int PEBBLE_TO_ANDROID_SUBSCRIBE_CH1 = 201;  // Arg(int32) = reading type.
    public static final int PEBBLE_TO_ANDROID_SUBSCRIBE_CH2 = 202;

    public static final int POINT_TYPE_TOD = 0;
    public static final int POINT_TYPE_TWD = 1;
    public static final int POINT_TYPE_TWS = 2;
    public static final int POINT_TYPE_HDG = 3;
    public static final int POINT_TYPE_SPD = 4;
    public static final int POINT_TYPE_COG = 5;
    public static final int POINT_TYPE_SOG = 6;



    public static final int APPMESSAGE_PING_INT = 0;
    public static final int APPMESSAGE_PONG_INT = 1;

    public static final int APPMESSAGE_PING_BYTES = 2;
    public static final int APPMESSAGE_PONG_BYTES = 3;


    /**
     * Add a summary table to the dictoionary for a specific channel
     * @param summary
     */

    public static void addToDict ( PebbleDictionary dict, SeatopPebbleChannel channel, SeatopPointSummaryStruct summary ) {
        dict.addBytes( channel.getAndroidToPebblePointSummaryMessageID(), summary.toByteBuffer().array() );
    }

    public static int numerator( double value ) {
        return (int) value;
    }

    public static int decimal( double value ) {
        return (int) ( ( value * 100 ) % 100 );
    }


    // ----------------------------------------------------------------------
    // Data Transformation Helpers


    /**
     * Turn the Aggregation information into the binary payload.
     * @param agg
     * @return
     */

    public static SeatopPointSummaryStruct convertAggregationToSummaryStruct ( SeatopFrequencyAggregator agg ) {

        SeatopPointSummaryStruct sum = new SeatopPointSummaryStruct();

        if ( agg.mostRecentPoint != null ) {
            sum.point_type = agg.mostRecentPoint.measurement.protocolPointType;

            sum.now_number = SeatopPebbleProtocol.numerator(agg.mostRecentPoint.timevalue);
            sum.now_decimal = SeatopPebbleProtocol.decimal(agg.mostRecentPoint.timevalue);
        }

        sum.max_number = SeatopPebbleProtocol.numerator(agg.pointMax.timevalue);
        sum.max_decimal = SeatopPebbleProtocol.decimal(agg.pointMax.timevalue);

        sum.min_number = SeatopPebbleProtocol.numerator(agg.pointMin.timevalue);
        sum.min_decimal = SeatopPebbleProtocol.decimal(agg.pointMin.timevalue);

        sum.full_avg_number = SeatopPebbleProtocol.numerator(agg.fullMean);
        sum.full_avg_decimal = SeatopPebbleProtocol.decimal(agg.fullMean);
        sum.full_avg_moving = SeatopPebbleProtocol.numerator(agg.fullMoving);

        sum.half_avg_number = SeatopPebbleProtocol.numerator(agg.halfMean);
        sum.half_avg_decimal = SeatopPebbleProtocol.decimal(agg.halfMean);
        sum.half_avg_moving = SeatopPebbleProtocol.numerator(agg.halfMoving);

        return sum;

    }

    // Approach 1: Send / Spray all data. Bad on battery life and ui refresh speed.


    // Approach 2: Direct Canvas Draws on Pebble. You are just my puppet.


    // Approach 3:  The pebble subscribes to specific feeds
    // remember that it needs to display 2 at once, top + bottom screen.


    // Approach 4:  The android provides a listing of available feeds.

    // Method:   listAvailablePointStreams
    //   Each datastream would have it's own ID, doesn't necessarily need to be
    // enumerated and compiled.

    // Response:  Remember it's a DICT <int, [int,String]>
    //  Configuration is easier on the android side -- I can see where the watch devs have gone.
    // The android side will have a data register -- and I would guess it makes 100% of that register
    // avialable to the watch side for visualization

    // Channel PA --  PointStream ( id, unit, name, lastHeardOn, totalHeardCount );

    // tuple key   = NMEA_HDG_LAST_HEARD   <-- These CAN be compiled in, they are standards.
    //                                         The units are always static ( compiled )
    // tuple value = long timestamp ms

    // tuple key   = NMEA_HDG_TOTAL_HEARD
    // tuple value = int 32 , count.

    /*
    {
        "NMEA_HDG_LAST_HEARD" => 10020302302030,
        "NMEA_HDG_TOTAL_HEARD" => 232
    }
*/

    // Pebble UI could hide the items that it hasn't ever seen from the UI. to reduce noise.
    // So what we're saying here is that the dictionary is predefined? YES. That's exactly what we are saying
    // This is not enterprise CORBA style. If we need to do a watch recompile because someone invents a
    // new way to measure sail shape, then yeah, we recompile and reinstall.

    // ahhhhh, the classic parallel dictionary design. I really dont want to implement subobject/subvalue
    // parsing logic, that makes it fragile / network byte order dependent. Remember lucifer.
    // Remember how much fun you had at pinpoint in 2001 doing the sunOs port? late nights, no drama.
    // This is surprisingly similar, 2001 -> 2016 , 15 years later. Total fucking throwback.

    // so let's talk about data transfer

/*
    {
        "NMEA_HDG_NOW" => 1625,
        "NMEA_HDG_MAX" => 1630,  // ooh - do we have a double? if not, let's use the multiplier.
        "NMEA_HDG_AVG" => 1600,

        "NMEA_HDG_HISTO_1" => ( 1550, new 64, seen 64, old )
    }
    */
        // Can we transfter structures across ble? Need to look at examples

        // or use two integers one for  whole numer, one for decimal / 100 <- 4 bits. short int.
        // The other readings is 0-359 , 8 bits.
        // uint8_t numerator // 1bytes
        // uint8_t decimal   // 1bytes

        // uint16_t degrees // 2bytes

        // how does tricorder serialize stucutres over the bluewire? uses the datalogging,
        //    } else if (type == TupleType.BYTES) {
    //        length = ((byte[]) value).length;
    //} else if (type == TupleType.STRING) {
        // The real question is how the Android side app decodes it.
        // Because we need to create a java struct that can be automatically destructed by pebble.

    /**
     *From Tricorder
     typedef struct __attribute__((__packed__)) {
       uint32_t packet_id;      // 4 bytes
       time_t timestamp;        // 4 bytes   Cases where timestamp is over the blue wire.
       uint16_t timestamp_ms;   // 2 bytes
       bool connection_status;  // 1 byte
       uint8_t charge_percent;  // 1 byte
       AccelData accel_data;    // 15 bytes   Struct w/in struct.
       int32_t crc32;           // 4 bytes
     } TricorderData;           // 31 bytes
     */


    /**
     * 2016Feb07 0800 - Morning design session of the Android -> Pebble BLE comm structure.

     1) Allow for Multiple Packets to be carried in a single message.

     This would be TWD and SPD.

    2) Should we enable the



     typedef struct seatop_current_frame {
        uint32_t packet_id;      // 4 bytes
        // time_t timestamp;        // 4 bytes   Cases where timestamp is over the blue wire.
        // uint16_t timestamp_ms;   // 2 bytes

     // Rather than deal with floating point errors, conversion, etc, let's just package two bytes together into numerator and decimal.
     // number is a synomym for numerator.

     // Ahh - have a summary object that's separate from the historgram frame infrormation


     enum {


     ANDROID_TO_PEBBLE_POINT_SUMMARY_CH1 = 110,
     ANDROID_TO_PEBBLE_POINT_DISTRO_CH1 = 111,

     ANDROID_TO_PEBBLE_POINT_SUMMARY_CH2 = 120,
     ANDROID_TO_PEBBLE_POINT_DISTRO_CH2 = 121,

     ANDROID_TO_PEBBLE_ALARM_MESSAGE = 160,
     ANDROID_TO_PEBBLE_ALARM_POINT_TYPE = 161,

     // For now, support two channels of information.

     PEBBLE_TO_ANDROID_SUBSCRIBE_CH1 = 210,  // Arg(int32) = reading type.
     PEBBLE_TO_ANDROID_SUBSCRIBE_CH2 = 220,  // Arg(int32) = reading type.


     }


     enum {
        POINT_TYPE_TWD = 1,
        POINT_TYPE_TWS = 2,
        POINT_TYPE_HDG = 3,
        POINT_TYPE_SPD = 4
        // pop out the nmea reference manual for the rest!
     }



     typedef struct seatop_point_summary {

     uint8_t point_type; // Would be duplicate information, also contained in the key, but make it easy for parsing?

     uint8_t now_number;
     uint8_t now_decimal;

     uint8_t max_number;
     uint8_t max_decimal;

     uint8_t min_number;
     uint8_t min_decimal;

     // Full Averages
     uint8_t full_avg_number;
     uint8_t full_avg_decimal;

     uint8_t full_moving_pos;  // If we' have positive or negative motion in the average of all points
     uint8_t full_moving_neg;

     // Half Averages - half of the points, the most recent ones.
     uint8_t half_avg_number;
     uint8_t half_avg_decimal;

     uint8_t half_moving_pos;  // If we' have positive or negative motion in the average
     uint8_t half_moving_neg;


     }   sizeof 15


     typedef struct seatop_distro_bucket {
     uint8_t new_count;
     uint8_t mid_count;
     uint8_t aged_count;
     } seatop_distro_bucket;   (sizeof 3)


     typedef struct seatop_point_distro {

     uint8_t point_type;

     uint8_t newest_bucket_number;
     uint8_t oldest_bucket_number;

     seatop_distro_bucket bucket_00;
     seatop_distro_bucket bucket_01;
     seatop_distro_bucket bucket_02;
     seatop_distro_bucket bucket_03;
     seatop_distro_bucket bucket_04;
     seatop_distro_bucket bucket_05;
     seatop_distro_bucket bucket_06;
     seatop_distro_bucket bucket_07;
     seatop_distro_bucket bucket_08;
     seatop_distro_bucket bucket_09;
     seatop_distro_bucket bucket_10;
     seatop_distro_bucket bucket_11;
     seatop_distro_bucket bucket_12;
     seatop_distro_bucket bucket_13;
     seatop_distro_bucket bucket_14;
     seatop_distro_bucket bucket_15;
     seatop_distro_bucket bucket_16;
     seatop_distro_bucket bucket_17;
     seatop_distro_bucket bucket_18;
     seatop_distro_bucket bucket_19;

     }
    // 20 * 3 + 3 = 63 bytes.




     bool connection_status;  // 1 byte
     uint8_t charge_percent;  // 1 byte
     AccelData accel_data;    // 15 bytes   Struct w/in struct.
     int32_t crc32;           // 4 bytes
     } TricorderData;           // 31 bytes
     */


}
