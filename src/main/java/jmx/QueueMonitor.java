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

package jmx;

import net.Queue;

/**
 * Created by luc on 9/4/14.
 */
public class QueueMonitor implements QueueMonitorMBean {
    protected final Queue<?> queue;
    private long prevTime;
    private long prevsize;
    private long rate;
    private final boolean queued;

    public QueueMonitor(Queue<?> queue, boolean queued) {
        this.queue = queue;
        this.queued = queued;
        this.prevTime = System.currentTimeMillis();
    }

    @Override
    public long getBackLog() {
        return queue.backlog();
    }

    @Override
    public int getPackets() {
        return queue.size();
    }

    @Override
    public long getRate() {
        long time = System.currentTimeMillis();
        long delta = time - prevTime;
        prevTime = time;
        if (delta >= 1000) {
            long size;
            if (queued) {
                size = queue.dequeued();
            } else {
                size = queue.queued();
            }
            long dsize = size - prevsize;
            prevsize = size;
            if (dsize >= 0) {
                rate = ((dsize * 1000) / delta);
            }
        }
        return rate;
    }

    @Override
    public void reset() {
        queue.reset();

    }

    @Override
    public long getDropped() {
        return queue.dropped();
    }

    @Override
    public int getMinDelay() {
        return queue.getDelay().getMin();
    }

    @Override
    public int getMeanDelay() {
        return queue.getDelay().getMean();
    }

    @Override
    public int getMaxDelay() {
        return queue.getDelay().getMax();
    }

    @Override
    public int getAvgDelay() { return queue.getDelay().getAvg();}
}
