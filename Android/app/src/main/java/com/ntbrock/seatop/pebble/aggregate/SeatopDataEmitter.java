package com.ntbrock.seatop.pebble.aggregate;

import java.util.Iterator;
import java.util.Vector;


/**
 * Created by brockman on 2/3/16.
 */
public class SeatopDataEmitter implements SeatopDataReceiver {

    Vector<SeatopDataReceiver> receivers = new Vector();
    Vector<SeatopDataPoint> lastPoints = new Vector();

    public void addReceiver(SeatopDataReceiver newReceiver) {
        receivers.add(newReceiver);
    }

    public void broadcastSinglePoint(SeatopDataPoint dataPoint) {
        lastPoints.add(dataPoint);

        for (Iterator i = receivers.iterator(); i.hasNext(); ) {
            SeatopDataReceiver receiver = (SeatopDataReceiver) i.next();
            receiver.handleSinglePoint(dataPoint);
        }
    }

    @Override
    public void handleSinglePoint(SeatopDataPoint dataPoint) {
        // Base Class Just emits straight though to all listeners
        broadcastSinglePoint(dataPoint);
    }

    public void startSimulatorThread() {

        SeatopEmitterThread t = new SeatopEmitterThread(this);
        t.start();

    }

}

/***
 * Simple Swing style thread to generate data points dynamically.
 */

class SeatopEmitterThread extends Thread {

    SeatopDataEmitter emitter;
    boolean shouldBeRunning = true;

    public SeatopEmitterThread(SeatopDataEmitter ref) {

        this.emitter = ref;
    }


    public void run ( ) {

        while ( shouldBeRunning ) {
            try {

                Thread.sleep(250);

                SeatopDataPoint newPoint = SeatopDataPoint.newTimeOfDaySeconds();

                // TODO, Generate more to add to set, like heading


                emitter.broadcastSinglePoint(newPoint);

            } catch ( InterruptedException x ) {
                shouldBeRunning = false;
            }
        }
    }
}
