package load;

import com.google.common.util.concurrent.RateLimiter;
import net.Queue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Created by luc on 9/3/14.
 */
public class loadGeneratorRunner implements Runnable {
    private Logger logger = LoggerFactory.getLogger(loadGeneratorRunner.class);
    RateLimiter limiter;
    private int id;
    double prevRate;
    private Random random=new Random();
    private Queue<net.Packet> queue;
    private LoadGeneratorMBean input;


    public loadGeneratorRunner(Queue<net.Packet> queue, LoadGeneratorMBean input) {
        this.limiter = RateLimiter.create(input.getRate(),1, TimeUnit.MICROSECONDS);
        this.queue = queue;
        this.input = input;
        this.prevRate = input.getRate();
    }

    public void setRate(Double rate) {
        this.limiter.setRate(rate);
    }

    @Override
    public void run() {
        Thread.currentThread().setName("load"+input.getPriority()+"-"+Thread.currentThread().getId());

        try {
            logger.info("starting {}", this);
            logger.info("rate: {}", input.getRate());
            limiter.setRate(input.getRate());
            while (input.isRunable()) {
                id++;
                logger.debug("send packet: {} prio:{}  size:{}", id, input.getPriority(), input.getPacketSize());
                queue.Queue(new net.Packet(input.getPacketSize(), input.getPriority(), id));
                int size=input.getPacketSize();
                //randomize some packet sizes delay
                if (input.getRandom()>0){
                    size=size+random.nextInt(input.getRandom());
                }
                limiter.acquire(input.getPacketSize()+random.nextInt(input.getPacketSize()/10));
                if (input.getRate() != prevRate) {
                    logger.info("update rate to {}", input.getRate());
                    limiter.setRate(input.getRate());
                    prevRate = input.getRate();
                }
            }
        } catch (Throwable t) {
            logger.error("oeps load:", t);
        }
    }
}
