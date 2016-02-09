#pragma once

// Tuple Dictionary Keys for Message Passing
// Seatop Uses the concept of channels and subscriptions to not spray too much data
// over the Blue wire.
// The Android -> Pebble messages live in the 100 block
// The Pebble -> Android messages live in the 200 block

enum {
  ANDROID_TO_PEBBLE_INFO_MESSAGE = 100,        // Used for arbitrary text display of cstring
  ANDROID_TO_PEBBLE_POINT_SUMMARY_CH1 = 101,
  ANDROID_TO_PEBBLE_POINT_SUMMARY_CH2 = 102,

  ANDROID_TO_PEBBLE_POINT_DISTRO_CH1 = 111,
  ANDROID_TO_PEBBLE_POINT_DISTRO_CH2 = 112,

  // ANDROID_TO_PEBBLE_POINT_FORCESUB_CH1 = 121, // Future: Android can force a subscription phone side.
  // ANDROID_TO_PEBBLE_POINT_FORCESUB_CH2 = 122,

  ANDROID_TO_PEBBLE_ALARM_MESSAGE = 150,
  ANDROID_TO_PEBBLE_POINT_ALARM_CH1 = 151,
  ANDROID_TO_PEBBLE_POINT_ALARM_CH2 = 152,

  // For now, support two channels of information.
  PEBBLE_TO_ANDROID_SUBSCRIBE_SECONDS = 200,  // Pebble can choose how many seconds the aggregation runs over. applies to ALL channels.
  PEBBLE_TO_ANDROID_SUBSCRIBE_CH1 = 201,  // Arg(int32) = reading type.
  PEBBLE_TO_ANDROID_SUBSCRIBE_CH2 = 202,  // Arg(int32) = reading type.

};

// Seatop Channel 1 is the top of the watch, Channel 2 is the bottom of the watch.
enum { 
  CHANNEL_1 = 1,
  CHANNEL_2 = 2
};

enum {
  POINT_TYPE_TWD = 1,
  POINT_TYPE_TWS = 2,
  POINT_TYPE_HDG = 3,
  POINT_TYPE_SPD = 4
  // break out the nmea reference manual for the rest!
};

//----------------------------------------------------------------------
// structs that move over the bluewire Android -> Pebble that contain
// summarized information and statistics for rendering to user on pebble.
// The top number is signed, can be positive or negative, up to 32k
// Rather than using double precision, I split numbers into two, the decimal is always /100

typedef struct SeatopPointSummary {

  uint8_t point_type; // enum POINT_TYPE_TWD 

  int16_t now_number;   // Latest Reading // This sizes as 3 in the struct for some reason. hrmmmmm
  uint8_t now_decimal;

  int16_t max_number;   // Maximum Reading w/ in Time Window.
  uint8_t max_decimal;

  int16_t min_number;   // Minimum Reading w/in the time window.
  uint8_t min_decimal;

  // Full Averages - All the data within in the subcription window.
  int16_t full_avg_number;
  uint8_t full_avg_decimal;
  int8_t full_moving;  // If we' have positive or negative motion in the average of all points

  // Half Averages - half of the points, the most recent ones.
  int16_t half_avg_number;
  uint8_t half_avg_decimal;
  int8_t half_moving ;  // If we' have positive or negative motion in the average

} __attribute__((__packed__)) SeatopPointSummary;  // Sizeof = 20
// For those counting bytes: 
// Note that without attribute((__packed__)), the byte length is written into the struct for safety for another > 1 byte in len


// Given a specific bucket, the numer of new, mid distrutbution and the ones about to age out.
// There's a poor man's array of these in each distirbution
typedef struct SeatopDistroBucket {
  uint8_t new_count;
  uint8_t mid_count;
  uint8_t aged_count;
} __attribute__((__packed__)) SeatopDistroBucket; // sizeof = 3



typedef struct SeatopPointDistro {

  uint8_t point_type;

  // Picked 20 arbirarility.  This is a limitation of the protocol 
  // Will not allow of very large, dyanmically sized histograms.
  // It also means that smaller historgrams will transmit empty data.
  SeatopDistroBucket bucket[20]; // I can do pointer math here, make iteration easier and less verbose.

  uint8_t start_bucket_number; // Used for rendering with a subset of information
  uint8_t stop_bucket_number; 

  uint8_t youngest_bucket_number;
  uint8_t oldest_bucket_number;

} __attribute__((__packed__)) SeatopPointDistro;  // Size of = 65
