package jmx;

import net.PrioBucketQueue;

/**
 * Created by luc on 9/4/14.
 */
public class QuantumMonitor implements QuantumMonitorMBean {
    public long count;
    public long prevTime;
    public long prevCount;
    public long rate;
    private boolean fair;
    private PrioBucketQueue queue;

    public QuantumMonitor(PrioBucketQueue queue){
        prevTime=System.currentTimeMillis();
        this.queue=queue;
    }

    public void inc() {
        count++;
    }

    @Override
    public long getRate() {
        long time=System.currentTimeMillis();
        long dtime=time-prevTime;
        if (dtime>=1000){
            long dcount=count-prevCount;
            if (dcount>=0){
                rate=(dcount*1000/dtime);
            }
            prevCount=count;
            prevTime=time;
        }
        return rate;
    }

}
