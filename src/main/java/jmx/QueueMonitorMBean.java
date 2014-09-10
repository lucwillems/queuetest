package jmx;

/**
 * Created by luc on 9/4/14.
 */
public interface QueueMonitorMBean {
    public long getBackLog();
    public int getPackets();
    public long getRate();
    public void reset();
    public long getDropped();
    public int getMinDelay();
    public int getMeanDelay();
    public int getMaxDelay();
    public int getAvgDelay();
}
