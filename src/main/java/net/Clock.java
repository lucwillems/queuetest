/*
 * Copyright 2014 Luc Willems (T.M.M.)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

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