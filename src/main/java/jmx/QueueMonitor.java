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
}
