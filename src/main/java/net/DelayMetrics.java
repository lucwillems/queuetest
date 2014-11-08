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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by luc on 9/7/14.
 */
public class DelayMetrics {
    public static final int NaN = -1;
    private final Logger log = LoggerFactory.getLogger(DelayMetrics.class);
    private final int[] data;
    private final int[] filter;
    private int ptr;
    private int filterPtr;
    private final Lock lock = new ReentrantLock();
    private int min;
    private int max;
    private int mean;
    private long nextTime;
    private final long deltaTime;
    private int avg;


    public DelayMetrics(int size, long deltaTime) {
        data = new int[size];
        this.deltaTime = deltaTime;
        ptr = 0;
        nextTime = System.currentTimeMillis() + deltaTime;
        filter = new int[4];
    }

    public void add(int x) {
        //Delay can only be positive and > 0 usec.
        //in multicore systems, delay is measured using nanoTime() which could give
        //sporatic negative values or high jumps in time. for now we only use positive delays.
        //also the sporatic high jumps forward in time will also be ignored
        //this implementation takes for each 4 samples the mean ( 3 value after sorting) and use this as input
        //this cause sporatic single High/low/negative values be ignored as they will be sorted as last/first element.
        //this reduces the bad effect of using nanoTime() and CLOCK_MONOTONIC but you still see
        //some spikes cause by JIT and/or GC
        //this works as long that input feed > 4x size of our array
        if (x >= 0) {
            lock.lock();
            try {
                //filter high/low values by using mean of 4 samples
                if (filterPtr < filter.length) {
                    filter[filterPtr] = x;
                    filterPtr++;
                    return;
                }
                filterPtr = 0;
                Arrays.sort(filter);
                if (ptr >= data.length) {
                    System.arraycopy(data, 1, data, 0, data.length - 1);
                }
                data[ptr] = filter[2];//use filter mean value
                ptr = Math.min(ptr + 1, data.length - 1);
            } finally {
                lock.unlock();
            }
        }
    }

    public int getMin() {
        checkUpdate();
        return min;
    }

    private void checkUpdate() {
        long time = System.currentTimeMillis();
        if (time >= nextTime) {
            nextTime = time + deltaTime;
            lock.lock();
            try {
                if (ptr > 0) {
                    Arrays.sort(data, 0, ptr);
                    min = data[0];
                    max = data[ptr - 1];
                    mean = data[ptr * 66 / 100];
                    avg = 0;
                    for (int i = 0; i < ptr; i++) {
                        avg = avg + data[i];
                    }
                    avg = avg / ptr;
                    ptr = 0;

                } else {
                    min = NaN;
                    max = NaN;
                    mean = NaN;
                    avg = NaN;
                }
            } catch (Throwable t) {
                log.error("oeps: ", t);
            } finally {
                lock.unlock();
            }
        }
    }

    public int getMax() {
        checkUpdate();
        return max;
    }

    public int getAvg() {
        checkUpdate();
        return avg;
    }

    public int getMean() {
        checkUpdate();
        return mean;
    }

}
