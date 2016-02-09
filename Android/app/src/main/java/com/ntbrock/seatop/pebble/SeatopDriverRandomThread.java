package com.ntbrock.seatop.pebble;

import android.widget.SeekBar;

import java.util.Iterator;
import java.util.Vector;

/**
 * Created by brockman on 2/6/16.
 */
public class SeatopDriverRandomThread extends Thread {

    boolean runThread = true;
    long sleepDelayMs = 1000;
    Vector<SeatopDriverNoisyBar> bars  = new Vector();

    public SeatopDriverRandomThread(long sleepDelayMs) {
        this.sleepDelayMs = sleepDelayMs;
    }

    public void addNoisyBarToRandomize( SeatopDriverNoisyBar seekbar ) {
        bars.add(seekbar);
    }

    public void run() {


        while ( runThread ) {

            try {

                Thread.sleep(sleepDelayMs);

                for ( Iterator i = bars.iterator(); i.hasNext(); ) {
                    SeatopDriverNoisyBar bar = (SeatopDriverNoisyBar) i.next();
                    bar.randomizeValue();
                }

            } catch ( InterruptedException x ) {
                runThread = false;
            }
        }

    }


}
