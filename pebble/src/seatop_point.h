#pragma once

#include <pebble.h>

// Global Singleton values 
SeatopPointSummary channel1_summary;
SeatopPointSummary channel2_summary;
SeatopPointDistro channel1_distro;
SeatopPointDistro channel2_distro;

int on_inbox_received_seatop_message ( Tuple *t );

void on_point_summary_recv( int channel, SeatopPointSummary *summary );
