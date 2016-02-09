#pragma once

//---------------------------
// Trending arrow code

#define MOVING_PCT 10
#define MOVING_FAST_PCT 30


//-----------------------------------------------------

void on_seatop_window_load( Window * window );
void on_seatop_window_unload( Window * window );

void render_summary ( int channel, SeatopPointSummary * summary );




/** For Reference
static PebbleFont pebble_fonts[] = {
 { .name = "Gothic", .variant = "14", .res = FONT_KEY_GOTHIC_14 },
 { .name = "Gothic", .variant = "14 Bold", .res = FONT_KEY_GOTHIC_14_BOLD },
 { .name = "Gothic", .variant = "18", .res = FONT_KEY_GOTHIC_18 },
 { .name = "Gothic", .variant = "18 Bold", .res = FONT_KEY_GOTHIC_18_BOLD },
 { .name = "Gothic", .variant = "24", .res = FONT_KEY_GOTHIC_24 },
 { .name = "Gothic", .variant = "24 Bold", .res = FONT_KEY_GOTHIC_24_BOLD },
 { .name = "Gothic", .variant = "28", .res = FONT_KEY_GOTHIC_28 },
 { .name = "Gothic", .variant = "28 Bold", .res = FONT_KEY_GOTHIC_28_BOLD },

 { .name = "Bitham", .variant = "30 Black", .res = FONT_KEY_BITHAM_30_BLACK },
 { .name = "Bitham", .variant = "42 Bold", .res = FONT_KEY_BITHAM_42_BOLD },
 { .name = "Bitham", .variant = "42 Light", .res = FONT_KEY_BITHAM_42_LIGHT },

 { .name = "Bitham", .variant = "34 Medium Numbers", .res = FONT_KEY_BITHAM_34_MEDIUM_NUMBERS },
 { .name = "Bitham", .variant = "42 Medium Numbers", .res = FONT_KEY_BITHAM_42_MEDIUM_NUMBERS },

 { .name = "Roboto", .variant = "21 Condensed", .res = FONT_KEY_ROBOTO_CONDENSED_21 },
 { .name = "Roboto", .variant = "49 Bold Subset", .res = FONT_KEY_ROBOTO_BOLD_SUBSET_49 },
 { .name = "Droid",  .variant = "28 Bold", .res = FONT_KEY_DROID_SERIF_28_BOLD },
 { .name = "LECO", .variant = "20 Bold Numbers", .res = FONT_KEY_LECO_20_BOLD_NUMBERS },
 { .name = "LECO", .variant = "32 Bold Numbers", .res = FONT_KEY_LECO_32_BOLD_NUMBERS },
 { .name = "LECO", .variant = "36 Bold Numbers", .res = FONT_KEY_LECO_36_BOLD_NUMBERS },
 { .name = "LECO", .variant = "38 Bold Numbers", .res = FONT_KEY_LECO_38_BOLD_NUMBERS },
 { .name = "LECO", .variant = "28 Light Numbers", .res = FONT_KEY_LECO_28_LIGHT_NUMBERS }
*/
