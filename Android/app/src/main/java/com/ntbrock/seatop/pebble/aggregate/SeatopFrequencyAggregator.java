package com.ntbrock.seatop.pebble.aggregate;

import java.util.Vector;



/**
 * A basic stats package for freuency counting, bucketing.
 *
 * SeatopStatFrequency implements SeatopDataReceiver with a filter
 * so that it can receive points from any emitter.
 *
 *
 * Created by brockman on 2/3/16.
 * Ported by brockman on 2/7/16.
 */



public class SeatopFrequencyAggregator implements SeatopDataReceiver {

    public static int RECOMMENDED_BUCKET_COUNT = 19;

    // Keep some limited data storage, determined by lifetime.
    Vector<SeatopDataPoint> livingPoints = new Vector();
    SeatopDataLifetime lifetime;
    SeatopDataFilter filter;
    int numBuckets;
    SeatopStatBucket[] buckets = new SeatopStatBucket[0];


    // Stored calculations from teh last run of calcultate();
    public int pointCount;

    public SeatopDataPoint mostRecentPoint;

    public SeatopDataPoint pointMax;
    public SeatopDataPoint pointMin;

    public double fullSum;
    public double fullMean;
    public int fullMoving;

    public double halfSum;
    public double halfMean;
    public int halfMoving;

    public int frequencyMaxTotal;
    public int frequencyMaxBucket;
    public int frequencyMinTotal;
    public int frequencyMinBucket;

    public SeatopFrequencyAggregator( int numBuckets,
                                SeatopDataFilter filter,
                                SeatopDataLifetime lifetime ) {
        this.numBuckets = numBuckets;
        this.lifetime = lifetime;
        this.filter = filter;

    }

    /**
     * Callers can ask if the aggregation is ready. Hide away the internals.
     * @return
     */

    public boolean isReady() {
        return this.mostRecentPoint != null;
    }

    @Override
    public void handleSinglePoint(SeatopDataPoint dataPoint) {

        // Skip this point if it doesn't match the filter, keep our stored data much smaller.
        if ( ! filter.isPointInteresting(dataPoint) ) { return; }

        System.out.println("[SeatopStatFrequency:65] Filter: " + filter.measurement + "  Incoming DataPoint: "+ dataPoint);

        ingressPoint(dataPoint);

        calculate();

        reconstructBuckets();

        bucketize();

    }


    private int getBucketNumberForValue(double pointValue) {

        // Gracefully (?) handle two extreme cases, out of bounds.
        if ( pointValue <= pointMin.timevalue ) { return 0; }
        if ( pointValue >= pointMax.timevalue ) { return buckets.length-1; }

        // use divided linear search.
        /*
        if ( pointValue > pointMean ) {
            // Start at end
        } else {
            // Start at begin

        }*/

        // Simple , inefficient, minimum linear search for now. Optimize later.
        for ( int b = 0; b < buckets.length ; b++ ) { // Changed to reverse mode for fun

            // System.out.println("SCANNING Bucket[" + b + "] MinValue: " +buckets[b].minValue + " MaxValue: "+ buckets[b].maxValue );


            if ( buckets[b].minValue <= pointValue && pointValue < buckets[b].maxValue ) {
                return b;
            }
        }

        // ---------------------------------------------------------------
        // No bucket found!! Ruh Oh
        // WE are about to explode, print some debugging.
        for ( int b = 0; b < buckets.length; b++ ) {
            System.err.println("DEBUG Bucket[" + b + "] MinValue: " + buckets[b].minValue + " MaxValue: " + buckets[b].maxValue);
        }
        throw new RuntimeException("[SeatopStatFrequency:90] No suitable bucket found for value: " + pointValue + ".  Should have been caught in max/min defense: Min: "+ pointMin.timevalue + " Max: " + pointMax.timevalue);
    }


    /**
     * Scan the living dataset and re-calculcate bucket frequency.
     */

    public void bucketize() {
        // Efficiency question - better to loop by buckets or by datapoints? points!


        if ( buckets != null  && buckets.length > 0 ) {

            // zero out bucket values -- Could optimize this later with deltas.
            for (int j = 0; j < buckets.length; j++) { buckets[j].zero(); }


            for (int i = 0; i < livingPoints.size(); i++) {
                SeatopDataPoint point = livingPoints.elementAt(i);

                int bucketNumber = getBucketNumberForValue(point.timevalue);
                SeatopStatBucket bucket = buckets[bucketNumber];

                // what cycle of life are we in?
                if ( lifetime.isPointNew(point) ) {
                    bucket.numNew += 1;
                    bucket.numTotal += 1;
                } else if ( lifetime.isPointMidlife(point) ) {
                    bucket.numMidlife += 1;
                    bucket.numTotal += 1;
                } else if ( lifetime.isPointAged(point) ) {
                    bucket.numAged += 1;
                    bucket.numTotal += 1;
                } else if ( lifetime.isPointExpired(point)) {
                    // NO-Operation
                } else {
                    throw new RuntimeException("[SeatopStatFrequency:129] Caught a point that was in an uncretain state of life: "+ point);
                }


            }

            // Another loop to detect new min + max values.

            int newFreqMaxTotal = 0;
            int newFreqMaxBucket = 0;
            int newFreqMinTotal = 0;
            int newFreqMinBucket = 0;

            // Find the biggest bucket and the most frequency
            // Accelerates the Height of the visual
            for (int j = buckets.length-1; j >= 0; j--) {
                if (buckets[j].numTotal > newFreqMaxTotal) {
                    newFreqMaxTotal = buckets[j].numTotal;
                    newFreqMaxBucket = j;
                }

                if (buckets[j].numTotal > newFreqMinTotal) {
                    newFreqMinTotal = buckets[j].numTotal;
                    newFreqMinBucket = j;
                }
            }

            this.frequencyMaxTotal = newFreqMaxTotal;
            this.frequencyMaxBucket = newFreqMaxBucket;
            this.frequencyMinTotal = newFreqMinTotal;
            this.frequencyMinBucket = newFreqMinBucket;

        }

    }

    /**
     * Destroy and re-initialize the bucket array.
     * This is helpful to happen when min or max changes.
     */
    public void reconstructBuckets() {

        // the domain is the max-min
        double domain = pointMax.timevalue - pointMin.timevalue;
        if ( domain <= 0 ) {
            System.err.println("[SeatopStatFrequency:64] Refusing to create a zero width domain");
            return;
        }

        double perBucketDomain = domain / numBuckets;

        // Completely Build the buckets! trying array storage here.

        buckets = new SeatopStatBucket[numBuckets];

        for ( int b = 0; b < numBuckets; b++ ) {
            buckets[b] = new SeatopStatBucket();
            buckets[b].minValue = pointMin.timevalue + b * perBucketDomain;
            buckets[b].maxValue = pointMin.timevalue + (b+1) * perBucketDomain;

            //System.out.println("Rebuilding Bucket[" + b + "] MinValue: " +buckets[b].minValue + " MaxValue: "+ buckets[b].maxValue );

        }

        // Sanity check that the max on the final bucket == domainMax

        if ( buckets[numBuckets-1].maxValue != pointMax.timevalue ) {
            String message  = "Error Bucketizing, Sanity check: Final Bucket Max ("+buckets[numBuckets-1].maxValue+") was != Domain Max ("+pointMax.timevalue+")";
            System.err.println(message);
            // This happened only one time during testing with one of those double preceision point errors, of by 0.000000000001
            // throw new RuntimeException(message);
        }

    }



    public void calculate() {

        if ( livingPoints.size() <= 0 ) { return; } // no work to do.
        int newCount = 0;
        double newSum = 0.0;
        SeatopDataPoint newMax = livingPoints.firstElement();
        SeatopDataPoint newMin = livingPoints.firstElement();


        for ( int i = 0; i < livingPoints.size(); i++ ) {
            SeatopDataPoint point = livingPoints.elementAt(i);

            if ( ! lifetime.isPointExpired(point) ) { // Expired points will be collected on the next sweep.
                // Max
                if (point.timevalue > newMax.timevalue) {
                    newMax = point;
                }

                // Min
                if (point.timevalue < newMin.timevalue) {
                    newMin = point;
                }

                // Count
                newCount += 1;
                newSum += point.timevalue;
            }
        }

        double newMean = newSum / (double)newCount;


        // Stored calculations from teh last run of calcultate();
        this.pointCount = newCount;
        this.fullSum = newSum;
        this.fullMean = newMean;
        this.pointMax = newMax;
        this.pointMin = newMin;

        // This becomes a percentage.
        // Important Calculation for usability
        // Going with the difference between the NOW value -vs- the total average as a percentage of the recently seen range.
        this.fullMoving = (int) ( ( this.mostRecentPoint.timevalue - newMean ) / ( this.pointMax.timevalue - this.pointMin.timevalue )  * 100.0 ); // Pct relative.
        this.halfMoving = (int) ( this.mostRecentPoint.timevalue - 0 );

    }



    /**
     * Store the point into internal memory, dropping any dead points.
     */

    public void ingressPoint(SeatopDataPoint dataPoint) {

        int deadCount = 0;
        int iDead = -1;

        for ( int i = 0; i < livingPoints.size(); i++ ) {
            SeatopDataPoint point = livingPoints.elementAt(i);

            if ( lifetime.isPointExpired(point) ) {
                iDead = i;
                deadCount++;
            }

        }

        if ( iDead >= 0 ) {
            livingPoints.setElementAt(dataPoint, iDead);
        } else {
            // Grow the array
            livingPoints.add(dataPoint);
        }


        // MAke a 2nd graveyard pass only if needed.
        if ( deadCount > 1 ) {
            // Multiple points expired
            // System.err.println("[SeatopStatFrequency] Multiple points expired: " + deadCount );
            for ( int i = 0; i < livingPoints.size(); i++ ) {
                SeatopDataPoint point = livingPoints.elementAt(i);
                if ( lifetime.isPointExpired(point) ) { livingPoints.remove(i); }
            }
        }

        mostRecentPoint = dataPoint;
    }


    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append(" Min=" + pointMin.timevalue + "Max=" + pointMax.timevalue + " Mean=" + (int)fullMean + " Buckets=" + numBuckets + " FreqMax=" + frequencyMaxTotal + " in Bucket=" + frequencyMaxBucket + "   ");
        // Iterate Buckets
        if ( buckets != null && buckets.length > 0 ) {
            for (int i = 0; i < buckets.length; i++) {
                sb.append(buckets[i].numTotal + " " );
            }
        }
        sb.append ("  ");

        // Iterate Buckets Again for ageyness
        if ( buckets != null && buckets.length > 0 ) {
            for (int i = 0; i < buckets.length; i++) {
                sb.append(buckets[i].numNew + "/" + buckets[i].numMidlife + "/" + buckets[i].numAged + " " );
            }
        }


        return sb.toString();
    }
}
