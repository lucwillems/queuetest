package jmx;

/**
 * Created by luc on 9/4/14.
 */
public interface BucketQueueMonitorMBean extends QueueMonitorMBean {
    public int getAllocated();
    public int getBorrowed();
    public int getLended();
    public int getQuantum();
}
