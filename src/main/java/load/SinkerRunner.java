package load;

import com.google.common.util.concurrent.RateLimiter;
import net.Packet;
import net.Queue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Created by luc on 9/4/14.
 */
public class SinkerRunner implements Runnable {
    private final Logger log= LoggerFactory.getLogger(SinkerRunner.class);
    private final Queue<Packet> queue;
    private final SinkerMBean input;
    private final RateLimiter limiter;
    private double prevRate;
    public SinkerRunner(Queue<Packet> queue,SinkerMBean input) {
        this.queue=queue;
        this.input=input;
        this.limiter=RateLimiter.create(input.getRate(),1, TimeUnit.MICROSECONDS);
        this.prevRate=input.getRate();
    }



    @Override
    public void run() {
        Thread.currentThread().setName("sinker-"+Thread.currentThread().getId());
        try {
            log.info("start sinker");
            while (input.isRunable()) {
                Packet p = queue.deQueue();
                if (p != null) {
                    log.debug("got  id: {} prio:{} size:{}", p.id, p.prio, p.size);
                    limiter.acquire(p.size);
                } else {
                    log.info("got NULL, waiting for input...");
                    try {
                            Thread.sleep(5);
                    } catch (InterruptedException ignore) {
                    }
                    log.debug("found input, lets rock and roll");
                }
                if (input.getRate() != prevRate) {
                    log.info("new sinker rate: {}", input.getRate());
                    limiter.setRate(input.getRate());
                    prevRate = input.getRate();
                }
            }
        } catch(Throwable t) {
                log.error("oeps:sinker dead",t);
        }
        log.info("sinker done");
    }
}
