package jmx;

import net.BucketQueue;
import net.Queue;

/**
 * Created by luc on 9/4/14.
 */
public class BucketQueueMonitor extends QueueMonitor implements BucketQueueMonitorMBean {

    public BucketQueueMonitor(Queue<?> queue, boolean queued) {
        super(queue, queued);
    }

    @Override
    public int getAllocated() {
        return ((BucketQueue)queue).getAllocated();
    }

    @Override
    public int getBorrowed() {
        return ((BucketQueue)queue).getBorrowed();
    }

    @Override
    public int getLended() {
        return ((BucketQueue)queue).getLended();
    }

    @Override
    public int getQuantum() {
        return ((BucketQueue)queue).getQuatum();
    }
}
