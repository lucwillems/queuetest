package load;

import net.Packet;
import net.Queue;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by luc on 9/4/14.
 */
public class LoadGenerator implements LoadGeneratorMBean {
    private org.slf4j.Logger log= LoggerFactory.getLogger(LoadGenerator.class);
    private int packetSize=1000;
    private double rate=100000;
    boolean runnable=false;
    private int prio;
    ScheduledExecutorService executor;
    Queue<Packet> queue;
    private int random;

    public LoadGenerator(ScheduledExecutorService executor,Queue<Packet> queue, int p) {
        this.executor=executor;
        this.queue=queue;
        this.prio=p;
    }

    @Override
    public int getPriority() {
        return prio;
    }

    @Override
    public int getPacketSize() {
        return packetSize;
    }

    @Override
    public void setPacketSize(int s) {
        log.info("set packet size: {}",s);
        this.packetSize=s;
    }

    @Override
    public double getRate() {
        return rate;
    }

    @Override
    public void setRate(double r) {
        log.info("set rate: {}",r);
        this.rate=r;
    }

    @Override
    public void start() {
        runnable=true;
        log.info("Start load generator "+prio);
        executor.submit(new loadGeneratorRunner(queue, this));
        //todo : keep track and handle futures
    }

    @Override
    public void stop() {
        log.info("Stop load generator "+prio);
        runnable=false;
    }

    public boolean isRunable() {
        return runnable;
    }

    @Override
    public int getRandom() {
        return random;
    }

    @Override
    public void setRandom(int r) {
        random=r;
    }
}
