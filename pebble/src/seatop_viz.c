/**
 * Place all seatop specific logic in this file. The main.c should remain mostly skeleton
 */
#include <pebble.h>
#include "seatop.h"
#include "seatop_protocol.h"
#include "seatop_point.h"
#include "seatop_viz.h"

static TextLayer *s_channel1_value_layer;
static TextLayer *s_channel1_back_layer;
static Layer *s_channel1_canvas;

static TextLayer *s_channel2_value_layer;
static TextLayer *s_channel2_back_layer;
static Layer *s_channel2_canvas;

// Wow, this was a lot of pixel tweaking, but it looks great now
#define TEXTBOX_WIDTH 120
#define TEXTBOX_HEIGHT 32  // 28 for gothic bold 24

#define BACKBOX_WIDTH 90
#define BACKBOX_HEIGHT 20
#define BACKBOX_OFFSET 7

#define CHANNEL_PADDING -2


/**
 * Callback for the requst for the layer to redraw itself!
 * I see other devlopers using layer.invalidate() quite a bit.
 */
#define ANTIALIASING true

static void on_canvas_layer_draw(Layer *layer, GContext *ctx) {
  // Render and logic
  // APP_LOG(APP_LOG_LEVEL_INFO, "Rendering layer");

  int channel = 0;
  if ( layer == s_channel1_canvas ) { channel = 1; }
  else if ( layer == s_channel2_canvas ) { channel = 2; } // Probably supposed to pass this in context ;)
  
  graphics_context_set_antialiased(ctx, ANTIALIASING);

  GRect bounds = layer_get_bounds(layer);

  if ( channel == 1  ) { 
    APP_LOG(APP_LOG_LEVEL_INFO, "Rendering Channel 1");
    graphics_context_set_fill_color(ctx, GColorDarkGray);
  } else if ( channel == 2 ) { 
    APP_LOG(APP_LOG_LEVEL_INFO, "Rendering Channel 2");
    graphics_context_set_fill_color(ctx, GColorBlack);
  } else { 
    APP_LOG(APP_LOG_LEVEL_INFO, "Rendering Channel UNKNOWN");
    graphics_context_set_fill_color(ctx, GColorBlack);
  }

  graphics_fill_rect(ctx, bounds, 0, GCornerNone);
}


void on_seatop_window_load( Window * window ) { 
  // Bounding Dimensions
  Layer *window_layer = window_get_root_layer(window);
  GRect bounds = layer_get_bounds(window_layer);


  int channel1Y = bounds.size.h / 2 - TEXTBOX_HEIGHT - CHANNEL_PADDING;
  int channel2Y = bounds.size.h / 2 + CHANNEL_PADDING;
  
  //===============================
  // BACK Layer 1
  s_channel1_back_layer = text_layer_create( GRect(bounds.size.w/2 - BACKBOX_WIDTH/2, channel1Y + BACKBOX_OFFSET, BACKBOX_WIDTH, BACKBOX_HEIGHT ));
  text_layer_set_background_color(s_channel1_back_layer, GColorWhite);

  // TEXT VALUE CH 1
  s_channel1_value_layer = text_layer_create( GRect(bounds.size.w/2 - TEXTBOX_WIDTH/2, channel1Y, TEXTBOX_WIDTH, TEXTBOX_HEIGHT ));
  text_layer_set_background_color(s_channel1_value_layer, GColorClear);
  text_layer_set_text_color(s_channel1_value_layer, GColorBlack);
  text_layer_set_text_alignment(s_channel1_value_layer, GTextAlignmentCenter);
  text_layer_set_font(s_channel1_value_layer, fonts_get_system_font( FONT_KEY_GOTHIC_24_BOLD ));

  text_layer_set_text(s_channel1_value_layer, "CH1");

  // CANVAS 1
  s_channel1_canvas = layer_create(GRect(0, 0, bounds.size.w, bounds.size.h / 2));
  layer_set_update_proc(s_channel1_canvas, on_canvas_layer_draw);


  // BACK Layer 1
  s_channel2_back_layer = text_layer_create( GRect(bounds.size.w/2 - BACKBOX_WIDTH/2, channel2Y + BACKBOX_OFFSET, BACKBOX_WIDTH, BACKBOX_HEIGHT ));
  text_layer_set_background_color(s_channel2_back_layer, GColorWhite);

  //===============================
  // TEXT VALUE CH2
  s_channel2_value_layer = text_layer_create( GRect(bounds.size.w/2 - TEXTBOX_WIDTH/2, channel2Y, TEXTBOX_WIDTH, TEXTBOX_HEIGHT));
  text_layer_set_background_color(s_channel2_value_layer, GColorClear);
  text_layer_set_text_color(s_channel2_value_layer, GColorBlack);
  text_layer_set_text_alignment(s_channel2_value_layer, GTextAlignmentCenter);
  text_layer_set_font(s_channel2_value_layer, fonts_get_system_font(FONT_KEY_GOTHIC_24_BOLD));

  text_layer_set_text(s_channel2_value_layer, "CH2");

  // CANVAS 2
  s_channel2_canvas = layer_create(GRect(0, bounds.size.h / 2, bounds.size.w, bounds.size.h / 2));
  layer_set_update_proc(s_channel2_canvas, on_canvas_layer_draw);

  //--------------------------------------------------------------------------------
  // Add layers in order
  layer_add_child(window_layer, s_channel1_canvas);
  layer_add_child(window_layer, text_layer_get_layer(s_channel1_back_layer));
  layer_add_child(window_layer, text_layer_get_layer(s_channel1_value_layer));

  layer_add_child(window_layer, s_channel2_canvas);
  layer_add_child(window_layer, text_layer_get_layer(s_channel2_back_layer));
  layer_add_child(window_layer, text_layer_get_layer(s_channel2_value_layer));
}


void on_seatop_window_unload( Window * window ) { 

  text_layer_destroy(s_channel1_value_layer);
  text_layer_destroy(s_channel1_back_layer);
  layer_destroy(s_channel1_canvas);

  text_layer_destroy(s_channel2_value_layer);
  text_layer_destroy(s_channel2_back_layer);
  layer_destroy(s_channel2_canvas);

}

// To fix weird memory display issues , moving these to a more private location 
// Reference pointers
static char * moving_none = "  ";
static char * moving_down = " <";
static char * moving_down_fast = "<<";
static char * moving_up = "> ";
static char * moving_up_fast = ">>";


#define CHANNEL_BUFLEN 20
static char s_channel2_buffer[CHANNEL_BUFLEN];
static char s_channel1_buffer[CHANNEL_BUFLEN];

void render_summary ( int channel, SeatopPointSummary * summary ) { 

  // Print the << >> average motion visuals

  if ( channel == CHANNEL_1 ) { 
    char *up1 = moving_none;
    char *dn1 = moving_none;

    if ( summary->full_moving < ( -1 * MOVING_PCT ) ) { dn1 = moving_down; }
    if ( summary->full_moving < ( -1 * MOVING_FAST_PCT ) ) { dn1 = moving_down_fast; }
    
    if ( summary->full_moving > MOVING_PCT ) { up1 = moving_up; }
    if ( summary->full_moving > MOVING_FAST_PCT ) { up1 = moving_up_fast; }
    
    
    // TODO , formatting should be dynamic based on point type. This is hardcoded.
    snprintf( s_channel1_buffer, sizeof(s_channel1_buffer), "%s %03d M %s", dn1, summary->now_number, up1 );
    text_layer_set_text(s_channel1_value_layer, s_channel1_buffer );


  } else if ( channel == CHANNEL_2 ) { 

    char *up2 = moving_none;
    char *dn2 = moving_none;

    if ( summary->full_moving < ( -1 * MOVING_PCT ) ) { dn2 = moving_down; }
    if ( summary->full_moving < ( -1 * MOVING_FAST_PCT ) ) { dn2 = moving_down_fast; }
    
    if ( summary->full_moving > MOVING_PCT ) { up2 = moving_up; }
    if ( summary->full_moving > MOVING_FAST_PCT ) { up2 = moving_up_fast; }
    
    // No need for kt, it is implieed because there is no M.
    snprintf( s_channel2_buffer, sizeof(s_channel2_buffer), "%s %d.%01d %s", dn2, summary->now_number, summary->now_decimal, up2  );
    text_layer_set_text(s_channel2_value_layer, s_channel2_buffer);

    

  } else { 
    APP_LOG(APP_LOG_LEVEL_ERROR, "Unknown Channel Number during render of summary");
  }


}
