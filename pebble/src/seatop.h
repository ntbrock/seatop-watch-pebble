#pragma once
//---------------------------------------------------------------------------
// Generic Ping Strucutres, Kept in from Skeleton Application

enum { 
  APPMESSAGE_PING_INT = 0,
  APPMESSAGE_PONG_INT = 1,
  APPMESSAGE_PING_STRUCT = 2,
  APPMESSAGE_PONG_STRUCT = 3
};

typedef struct AppMessagePing { 

  uint32_t firstInt;
  uint32_t secondInt;
  uint32_t thirdInt;
} AppMessagePing;  // Size of = 12


void update_main_info( char * message);
