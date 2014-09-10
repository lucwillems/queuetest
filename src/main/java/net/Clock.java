package net;

/**
 * Created by luc on 9/9/14.
 */
public class Clock {
    public static long MAX_USEC=Long.MAX_VALUE/1000;
    public static long MAX_MSEC=Long.MAX_VALUE;

    /* TODO : calling this from 2 different threads when having usec delay could lead to
     * negative values which is caused by the fact that nanoTime is based on
     * CLOCK_MONOTONIC which has issues in multi core systems because it's based on each
     * core timers which are NEVER in sync. as stated by java doc, we can only use nanoTime()
     * to measure Delta time, but we also could have negative delta in case of a cpu switch.
     *
     * we should use CLOCK_BOOTTIME but this requires :
     *  - kernel > 2.6.39
     *  - jni or better jvm
     *
     *  note that according to testing my current resolution is +/- 40 usec
     */
    public static long getPacketTimeStamp(){
        return (System.nanoTime()/1000) & MAX_USEC;
    }

    public static long getCurrentMilliSeconds(){
        return System.currentTimeMillis() & MAX_MSEC;
    }
}