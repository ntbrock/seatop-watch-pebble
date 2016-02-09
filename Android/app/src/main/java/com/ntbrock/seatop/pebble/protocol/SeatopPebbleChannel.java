package com.ntbrock.seatop.pebble.protocol;

/**
 * Created by brockman on 2/7/16.
 */
public class SeatopPebbleChannel {
    // Channel 0 is effectively text and general messages.
    public static final int CHANNEL_NUMBER_1 = 1;
    public static final int CHANNEL_NUMBER_2 = 2;

    int channelNumber;

    public SeatopPebbleChannel(int channelNumber) {
        // Allow accelerating the constructor by the subscription messages as well.
        // I've tried to design the protocol so that the last # of the message Id is always the channel.
        switch ( channelNumber ) {
            case SeatopPebbleProtocol.PEBBLE_TO_ANDROID_SUBSCRIBE_CH1:
                this.channelNumber = SeatopPebbleChannel.CHANNEL_NUMBER_1;
            case SeatopPebbleProtocol.PEBBLE_TO_ANDROID_SUBSCRIBE_CH2:
                this.channelNumber = SeatopPebbleChannel.CHANNEL_NUMBER_2;
            default:
                this.channelNumber = channelNumber;
        }
    }

    public int getChannelNumber() {
        return channelNumber;
    }

    public int getAndroidToPebblePointSummaryMessageID() {
        switch ( channelNumber ) {
            case SeatopPebbleChannel.CHANNEL_NUMBER_1:
                return SeatopPebbleProtocol.ANDROID_TO_PEBBLE_POINT_SUMMARY_CH1;
            case SeatopPebbleChannel.CHANNEL_NUMBER_2:
                return SeatopPebbleProtocol.ANDROID_TO_PEBBLE_POINT_SUMMARY_CH2;
            default:
                throw new IllegalArgumentException("SeatopPebbleChannel:24> Bad ChannelNumber: " + channelNumber);
        }

    }

    public int getAndroidToPebblePointDistroMessageID() {
        switch ( channelNumber ) {
            case SeatopPebbleChannel.CHANNEL_NUMBER_1:
                return SeatopPebbleProtocol.ANDROID_TO_PEBBLE_POINT_DISTRO_CH1;
            case SeatopPebbleChannel.CHANNEL_NUMBER_2:
                return SeatopPebbleProtocol.ANDROID_TO_PEBBLE_POINT_DISTRO_CH2;
            default:
                throw new IllegalArgumentException("SeatopPebbleChannel:24> Bad ChannelNumber: " + channelNumber);
        }

    }

    public int getAndroidToPebblePointAlarmMessageID() {
        switch ( channelNumber ) {
            case SeatopPebbleChannel.CHANNEL_NUMBER_1:
                return SeatopPebbleProtocol.ANDROID_TO_PEBBLE_POINT_ALARM_CH1;
            case SeatopPebbleChannel.CHANNEL_NUMBER_2:
                return SeatopPebbleProtocol.ANDROID_TO_PEBBLE_POINT_ALARM_CH2;
            default:
                throw new IllegalArgumentException("SeatopPebbleChannel:24> Bad ChannelNumber: " + channelNumber);
        }

    }

}
