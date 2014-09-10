package jmx;

/**
 * Created by luc on 9/4/14.
 */
public class QuantumMonitor implements QuantumMonitorMBean {
    private long count;
    private long prevTime;
    private long prevCount;
    private long rate;

    public QuantumMonitor(){
        prevTime=System.currentTimeMillis();
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
