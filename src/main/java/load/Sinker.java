package load;

import net.Packet;
import net.Queue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by luc on 9/4/14.
 */
public class Sinker implements SinkerMBean {
    private final Logger log= LoggerFactory.getLogger(Sinker.class);
    private final ScheduledExecutorService executor;
    private final Queue<Packet> queue;
    private double rate=1000000;
    private boolean runable;
    private long avgDelay;

    public Sinker(ScheduledExecutorService executor, Queue<Packet> queue) {
        this.executor=executor;
        this.queue=queue;
    }


    @Override
    public double getRate() {
        return rate;
    }

    @Override
    public void setRate(double r) {
        this.rate=r;
    }

    @Override
    public boolean isRunable() {
        return runable;
    }

    @Override
    public void start() {
        runable=true;
        executor.submit(new SinkerRunner(queue,this));
        log.info("sinker started");
    }

    @Override
    public void stop() {
        runable=false;
        log.info("sinker stopped");
    }
}
