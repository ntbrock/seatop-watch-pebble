package com.ntbrock.seatop.pebble;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SeekBar;
import android.widget.TextView;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;
import com.ntbrock.seatop.pebble.aggregate.SeatopDataEmitter;
import com.ntbrock.seatop.pebble.aggregate.SeatopDataFilter;
import com.ntbrock.seatop.pebble.aggregate.SeatopDataLifetime;
import com.ntbrock.seatop.pebble.aggregate.SeatopDataPoint;
import com.ntbrock.seatop.pebble.aggregate.SeatopFrequencyAggregator;
import com.ntbrock.seatop.pebble.aggregate.SeatopMeasurementType;
import com.ntbrock.seatop.pebble.protocol.SeatopPebbleProtocol;
import com.ntbrock.seatop.pebble.protocol.SeatopPointSummaryStruct;

import org.joda.time.DateTime;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.UUID;

public class SeatopDriverActivity extends AppCompatActivity {

    // private PebbleKit.PebbleDataLogReceiver mDataloggingReceiver; // Tricorder uses data log
    private PebbleKit.PebbleDataReceiver mDataloggingReceiver;

    private static final UUID SEATOP_UUID = UUID.fromString(SeatopPebbleProtocol.PEBBLE_APPLICATION_UUID);

    public SeatopDriverNoisyBar twd;
    public SeatopDriverNoisyBar tws;
    public SeatopDriverNoisyBar hdg;
    public SeatopDriverNoisyBar spd;

    public SeatopDataEmitter realtimeDataHub;

    public SeatopDataLifetime lifetime; // Lifetime is consistent across channels.

    public SeatopDataFilter filterChannel1;
    public SeatopDataFilter filterChannel2;

    public SeatopFrequencyAggregator aggregatorChannel1;
    public SeatopFrequencyAggregator aggregatorChannel2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seatop_driver);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView text = (TextView) findViewById(R.id.topText);
        text.setText("Hello Sea World, East of Bahamas\nLaunch: " + DateTime.now().toString());

        //-----------------------------------------------------------
        // Data Aggregation Implementation


        filterChannel1 = new SeatopDataFilter(SeatopMeasurementType.M_TWD);

        filterChannel2 = new SeatopDataFilter(SeatopMeasurementType.M_SPD);

        // lifetime = new SeatopDataLifetime(5, 55, 60, SeatopDataLifetime.SECONDS);
        lifetime = new SeatopDataLifetime(5, 15, 20, SeatopDataLifetime.SECONDS);


        aggregatorChannel1 = new SeatopFrequencyAggregator(
                SeatopFrequencyAggregator.RECOMMENDED_BUCKET_COUNT,
                filterChannel1, lifetime );

        aggregatorChannel2 = new SeatopFrequencyAggregator(
                SeatopFrequencyAggregator.RECOMMENDED_BUCKET_COUNT,
                filterChannel2, lifetime );


        // The emitter pattern creates a central hub for all data flow with subscriptions.

        realtimeDataHub = new SeatopDataEmitter();
        realtimeDataHub.addReceiver(aggregatorChannel1);
        realtimeDataHub.addReceiver(aggregatorChannel2);

        //--------------------------------------------------------------
        //Seekbar View Implementation

        double initialTwd = 320;
        double initialTws = 14.4;
        double initialHdg = 007; // In honor of Spirit of Singapore Bahamas Passage
        double initialSpd = 16.1;

        // TWD
        twd = new SeatopDriverNoisyBar(
                (SeekBar) findViewById(R.id.seekTwd),
                (TextView) findViewById(R.id.valueTwd),
                SeatopMeasurementType.M_TWD, initialTwd, 359, 1.0 );

        // TWS
        tws = new SeatopDriverNoisyBar(
                (SeekBar) findViewById(R.id.seekTws),
                (TextView) findViewById(R.id.valueTws),
                SeatopMeasurementType.M_TWS, initialTws, 32, 100.0 );


        // HDG
        hdg = new SeatopDriverNoisyBar(
                (SeekBar) findViewById(R.id.seekHdg),
                (TextView) findViewById(R.id.valueHdg),
                SeatopMeasurementType.M_HDG, initialHdg, 359, 1.0 );

        // SPD
        spd = new SeatopDriverNoisyBar(
                (SeekBar) findViewById(R.id.seekSpd),
                (TextView) findViewById(R.id.valueSpd),
                SeatopMeasurementType.M_SPD, initialSpd, 24, 100.0 );


        // Initialize the noisy bars, invite the woo girls

        SeatopDriverRandomThread randomDriver = new SeatopDriverRandomThread(200);
        randomDriver.addNoisyBarToRandomize(twd);
        randomDriver.addNoisyBarToRandomize(tws);
        randomDriver.addNoisyBarToRandomize(hdg);
        randomDriver.addNoisyBarToRandomize(spd);

        // each of the noisy guys also broadcasts to the datahub.
        twd.addReceiver(realtimeDataHub);
        tws.addReceiver(realtimeDataHub);
        hdg.addReceiver(realtimeDataHub);
        spd.addReceiver(realtimeDataHub);

        //-------------------------------------------------


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String message = "Hello from Android!";

                sendInfoMessageToPebble(message);

                Snackbar.make(view, "Sent> " + message, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });



        //---------------------------------------------------

        randomDriver.start();

        SeatopDriverNetworkThread networkThread = new SeatopDriverNetworkThread(this);
        networkThread.start();

    }


    /**
     * Eventually refactor into PebbleProtocol
     * @param message
     */
    public void sendInfoMessageToPebble(String message) {
        PebbleDictionary dict = new PebbleDictionary();

        dict.addString(SeatopPebbleProtocol.ANDROID_TO_PEBBLE_INFO_MESSAGE, message );

        PebbleKit.sendDataToPebble(getApplicationContext(), SEATOP_UUID, dict);
    }



    public void sendAggregatedValuesToPebble() {
        PebbleDictionary dict = new PebbleDictionary();

        // TODO IMPLEMENT: Management of Pebble's current channel subscriptions

        // Channel 1
        if ( aggregatorChannel1.isReady() ) {
            SeatopPointSummaryStruct channel1Summary =
                    SeatopPebbleProtocol.convertAggregationToSummaryStruct(aggregatorChannel1);

            SeatopPebbleProtocol.addToDict(dict, SeatopPebbleProtocol.CHANNEL_1, channel1Summary);

            // TODO - distrogram
        }


        // Channel 2
        if ( aggregatorChannel2.isReady() ) {
            SeatopPointSummaryStruct channel2Summary =
                    SeatopPebbleProtocol.convertAggregationToSummaryStruct(aggregatorChannel2);

            SeatopPebbleProtocol.addToDict(dict, SeatopPebbleProtocol.CHANNEL_2, channel2Summary);

            // TODO - histogram
        }

        PebbleKit.sendDataToPebble(getApplicationContext(), SEATOP_UUID, dict);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_seatop_driver, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}


/**
 * Quick and dirty scheduled thread for delivery to pebble on interval.
 */

class SeatopDriverNetworkThread extends Thread {

    public static final int SLEEP_MS = 666;
    SeatopDriverActivity activity;
    boolean shouldRun = true;

    public SeatopDriverNetworkThread(SeatopDriverActivity activity ) {
        this.activity = activity;
    }


    public void run() {

        try {
            // Initial Sleep Delay for good luck
            Thread.sleep(2 * SLEEP_MS);
        } catch ( InterruptedException x ) {
            shouldRun = false;
        }

        while ( shouldRun ) {

            try {
                Thread.sleep(SLEEP_MS);
                activity.sendAggregatedValuesToPebble();
            } catch (InterruptedException x ) {
                shouldRun = false;
            }
        }
    }

}
