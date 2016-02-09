/**
 * Place all seatop specific logic in this file. The main.c should remain mostly skeleton
 */
#include <pebble.h>
#include "seatop.h"
#include "seatop_protocol.h"
#include "seatop_point.h"
#include "seatop_viz.h"

/**
 * Extract method refactor for all of our specific point handlers here 
 */

int on_inbox_received_seatop_message ( Tuple *t ) { 
  char buffer[128];
  int channel = 0;

  switch ( t->key ) { 

  case ANDROID_TO_PEBBLE_INFO_MESSAGE:
    update_main_info(t->value->cstring);  
    break;

  case ANDROID_TO_PEBBLE_POINT_SUMMARY_CH1:
    // read the struct from the wire, defensively.
    if ( sizeof(SeatopPointSummary) != t->length ) { 
      snprintf( buffer, 128, "The tuple data length (%d) != expected local struct of SeatopPointSummary(%d)", t->length, sizeof(SeatopPointSummary) );
      APP_LOG(APP_LOG_LEVEL_ERROR, buffer);
    } else { 
      // Store to global memory.
      memcpy( &channel1_summary, t->value->data, sizeof(SeatopPointSummary));
      on_point_summary_recv( CHANNEL_1, &channel1_summary );
    }
    break;

  case ANDROID_TO_PEBBLE_POINT_SUMMARY_CH2:
    // read the struct from the wire, defensively.
    if ( sizeof(SeatopPointSummary) != t->length ) { 
      snprintf( buffer, 128, "The tuple data length (%d) != expected local struct of SeatopPointSummary(%d)", t->length, sizeof(SeatopPointSummary) );
      APP_LOG(APP_LOG_LEVEL_ERROR, buffer);
    } else { 
      // Store to global memory.
      memcpy( &channel2_summary, t->value->data, sizeof(SeatopPointSummary));
      on_point_summary_recv( CHANNEL_2, &channel2_summary );
    }
    break;

  default: 
    return 0;
  }

  // Successfully able to handle message
  return 1;
}


void on_point_summary_recv( int channel, SeatopPointSummary *summary ) { 
  
  // Adventures at C  
  /*
    if ( channel == 2 ) {
  char buffer[128];
  snprintf( buffer, 128, "SeatopPointSummary CH: %d: Now: %d.%d , Max: %d.%d , Min: %d.%d",
	    channel,
	    (int) summary->now_number, (int) summary->now_decimal,
	    (int) summary->max_number, (int) summary->max_decimal,
	    (int) summary->min_number, (int) summary->min_decimal );
  APP_LOG(APP_LOG_LEVEL_INFO, buffer);
  
  snprintf( buffer, 128, "SeatopPointSummary CH: %d: Avg_Full: %d.%d -> %d  Avg_Half: %d.%d -> %d",
	    channel,
	    (int) summary->full_avg_number, (int) summary->full_avg_decimal, (int) summary->full_moving,
	    (int) summary->half_avg_number, (int) summary->half_avg_decimal, (int) summary->half_moving );
  APP_LOG(APP_LOG_LEVEL_INFO, buffer);
  }
  */
  // Paint the screen
  render_summary ( channel, summary );
  
}
