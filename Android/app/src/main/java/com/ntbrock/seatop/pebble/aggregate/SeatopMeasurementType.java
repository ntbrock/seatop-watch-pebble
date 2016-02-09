package com.ntbrock.seatop.pebble.aggregate;

import com.ntbrock.seatop.pebble.protocol.SeatopPebbleProtocol;

/**
 * All is nicely packaged for you in a single java friendly data dictionary for programmer yachtsmen.
 *
 * Magic strings all delcared here!  What you really want to use the M_* constructs.
 * STrengthened up the class definition for measurement to avoid repeating myself
 * and carrying along all sorts of parallel objects like units, etc.
 *
 * Created by brockman on 2/7/16.
 */

public class SeatopMeasurementType
{
    public static String UNIT_TIME = "sec";
    public static String UNIT_MAGNETIC = "M";
    public static String UNIT_SECONDS = "sec";
    public static String UNIT_KNOTS = "kt";

    public static String FORMATTER_TIME = "%d %s";
    public static String FORMATTER_COMPASS = "%03d %s";
    public static String FORMATTER_SPEED = "%.2f %s";

    //--------------------------------------------------------------------------------
    // Full Seatop Data Dictionary

    // Time of Day
    public static String SHORT_TOD = "TOD";
    public static String LONG_TOD = "Time of Day";
    public static SeatopMeasurementType M_TOD = new SeatopMeasurementType(
            SeatopPebbleProtocol.POINT_TYPE_TOD, SHORT_TOD, LONG_TOD, UNIT_TIME,FORMATTER_TIME );


    // True Wind Direction
    public static String SHORT_TWD = "TWD";
    public static String LONG_TWD = "True Wind Direction";
    public static SeatopMeasurementType M_TWD = new SeatopMeasurementType(
            SeatopPebbleProtocol.POINT_TYPE_TWD, SHORT_TWD, LONG_TWD, UNIT_MAGNETIC,FORMATTER_COMPASS );


    // True Wind Strength
    public static String SHORT_TWS = "TWS";
    public static String LONG_TWS = "True Wind Strength";
    public static SeatopMeasurementType M_TWS = new SeatopMeasurementType(
            SeatopPebbleProtocol.POINT_TYPE_TWS, SHORT_TWS, LONG_TWS, UNIT_KNOTS,FORMATTER_SPEED );


    // Heading
    public static String SHORT_HDG = "HDG";
    public static String LONG_HDG = "Heading";
    public static SeatopMeasurementType M_HDG = new SeatopMeasurementType(
            SeatopPebbleProtocol.POINT_TYPE_HDG, SHORT_HDG, LONG_HDG, UNIT_MAGNETIC, FORMATTER_COMPASS );


    // Boat Speed
    public static String SHORT_SPD = "SPD";
    public static String LONG_SPD = "Boat Speed";
    public static SeatopMeasurementType M_SPD = new SeatopMeasurementType(
            SeatopPebbleProtocol.POINT_TYPE_SPD, SHORT_SPD, LONG_SPD, UNIT_KNOTS, FORMATTER_SPEED );


    // Course over Ground
    public static String SHORT_COG = "COG";
    public static String LONG_COG = "Course Over Ground";
    public static SeatopMeasurementType M_COG = new SeatopMeasurementType(
            SeatopPebbleProtocol.POINT_TYPE_COG, SHORT_COG, LONG_COG, UNIT_MAGNETIC, FORMATTER_COMPASS );


    // Speed over Ground
    public static String SHORT_SOG = "SOG";
    public static String LONG_SOG = "Speed Over Ground";
    public static SeatopMeasurementType M_SOG = new SeatopMeasurementType(
            SeatopPebbleProtocol.POINT_TYPE_SOG, SHORT_SOG, LONG_SOG, UNIT_KNOTS, FORMATTER_SPEED );

    //--------------------------------------------------------------------------------

    public int protocolPointType;
    public String shortLabel;
    public String longLabel;
    public String units;
    public String formatter;

    public SeatopMeasurementType(
            int protocolPointType,
            String shortLabel,
            String longLabel,
            String units,
            String formatter
    ) {
        this.protocolPointType = protocolPointType;
        this.shortLabel = shortLabel;
        this.longLabel = longLabel;
        this.units = units;
        this.formatter = formatter;
    }

    public String toString() {
        return this.shortLabel + "." + this.units;
    }

    public boolean equals(SeatopMeasurementType other) {
        return other.shortLabel.equalsIgnoreCase(this.shortLabel);
    }


    public String format( double value ) {

        if ( this.formatter.equalsIgnoreCase(FORMATTER_COMPASS) ||
                this.formatter.equalsIgnoreCase(FORMATTER_TIME) ) {

            // %d
            return String.format(this.formatter, (int) value, units);

        } else {

            // %f
            return String.format(this.formatter, value, units);

        }

    }

}
