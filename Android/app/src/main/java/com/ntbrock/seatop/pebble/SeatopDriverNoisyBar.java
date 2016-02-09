package com.ntbrock.seatop.pebble;

import android.widget.SeekBar;
import android.widget.TextView;

import com.ntbrock.seatop.pebble.aggregate.SeatopDataEmitter;
import com.ntbrock.seatop.pebble.aggregate.SeatopDataPoint;
import com.ntbrock.seatop.pebble.aggregate.SeatopDataReceiver;
import com.ntbrock.seatop.pebble.aggregate.SeatopMeasurementType;

import java.util.Iterator;
import java.util.Vector;

/**
 * Created by brockman on 2/6/16.
 */
public class SeatopDriverNoisyBar extends SeatopDataEmitter {


    SeekBar seekbar;
    final TextView valueText;
    double initialValue;
    int maxValue;
    double multiplier = 1.0;
    final SeatopMeasurementType measurement;



    public SeatopDriverNoisyBar( SeekBar seekbar, TextView valueTextIn, SeatopMeasurementType measurementIn, double initialVaue, int maxValue, final double multiplier ) {
        this.seekbar = seekbar;
        this.valueText = valueTextIn;
        this.initialValue = initialVaue;
        this.maxValue = maxValue;
        this.multiplier = multiplier;
        this.measurement = measurementIn;


        seekbar.setMax((int) ( maxValue * multiplier ) );
        seekbar.setProgress((int) ( initialValue * multiplier ) );

        // This triggers when noisy values change as well as human interaction

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                // Chane display
                valueText.setText( measurement.format(progress / multiplier ) );

                // Broadcast to aggregator
                SeatopDataPoint p = new SeatopDataPoint(progress/multiplier, measurement);
                broadcastSinglePoint( p );
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });



    }

    public double getValue() {
        return seekbar.getProgress() / multiplier;
    }


    public void randomizeValue() {
        seekbar.setProgress( seekbar.getProgress() + sd6() );
    }

    /**
     * Yes, this is infact a signed six sided random dice roll. Hiiiii ya.
     * Have you met my Space Hamster Boo?!?!
     * @return
     */
    public int sd6() { return (int) ( Math.random() * 6.0 - 3.0 ); }

}
