package load;

import com.sun.xml.internal.bind.v2.runtime.reflect.Lister;
import jmx.BucketQueueMonitor;
import jmx.QuantumMonitor;
import jmx.QueueMonitor;
import net.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.*;
import java.lang.management.ManagementFactory;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Created by luc on 9/3/14.
 */
public class LoadTestApp {
    Logger log= LoggerFactory.getLogger(LoadTestApp.class);
    ScheduledExecutorService executor;
    MBeanServer mbs;
    LoadGenerator staticLoad0;
    LoadGenerator staticLoad1;
    LoadGenerator staticLoad2;
    LoadGenerator staticLoad3;
    LoadGenerator staticLoad4;
    LoadGenerator staticLoad5;
    Sinker sinker;
    PrioBucketQueue queue;
    QueueMonitor mainQueueIn;
    QueueMonitor mainQueueOut;
    BucketQueueMonitor bucket0;
    BucketQueueMonitor bucket1;
    BucketQueueMonitor bucket2;
    BucketQueueMonitor bucket3;
    BucketQueueMonitor bucket4;
    BucketQueueMonitor bucket5;
    QuantumMonitor quantumMonitor;
    LoadModulator modulator;

    private void regiserMbean(Object mbean,String name) {
        try {
            ObjectName oname = new ObjectName(name);
            mbs.registerMBean(mbean, oname);
        } catch (Exception e) {
            log.error("oeps: ", e);
        }
    }

    public void init() {
        mbs= ManagementFactory.getPlatformMBeanServer();
        queue=new PrioBucketQueue(6,5);
        quantumMonitor=new QuantumMonitor(queue);
        queue.init(new BucketEvents() {
                       @Override
                       public void onInit(BucketQueue bucket) {
                           simplePacketQueue queue = new simplePacketQueue();
                           bucket.setQueue(new simplePacketQueue());
                       }

                       @Override
                       public void onQuantumized(BucketQueue bucket) {
                           quantumMonitor.inc();
                           log.info("quantumize : {}: {}", bucket.getPriority(), bucket);
                       }

                       @Override
                       public void onBorrow(BucketQueue bucket, int size) {

                       }
                   }
        );
        int[] procents={5,10,50,25,10,10};//sum must be 100%
        int mtu=1500;
        queue.setup(procents, mtu);
        executor=new ScheduledThreadPoolExecutor(20);
        staticLoad0=new LoadGenerator(executor,queue,0);
        staticLoad1=new LoadGenerator(executor,queue,1);
        staticLoad2=new LoadGenerator(executor,queue,2);
        staticLoad3=new LoadGenerator(executor,queue,3);
        staticLoad4=new LoadGenerator(executor,queue,4);
        staticLoad5=new LoadGenerator(executor,queue,5);
        staticLoad0.setRate(1000);
        staticLoad1.setRate(1000);
        staticLoad2.setRate(50000);
        staticLoad3.setRate(50000);
        staticLoad4.setRate(3000);
        staticLoad5.setRate(40000);
        bucket0=new BucketQueueMonitor(queue.getBucket(0),true);
        bucket1=new BucketQueueMonitor(queue.getBucket(1),true);
        bucket2=new BucketQueueMonitor(queue.getBucket(2),true);
        bucket3=new BucketQueueMonitor(queue.getBucket(3),true);
        bucket4=new BucketQueueMonitor(queue.getBucket(4),true);
        bucket5=new BucketQueueMonitor(queue.getBucket(5),true);
        sinker=new Sinker(executor,queue);
        sinker.setRate(100000);
        mainQueueIn=new QueueMonitor(queue,false);
        mainQueueOut=new QueueMonitor(queue,true);
        modulator=new LoadModulator(executor,sinker);
        modulator.setMaximum(150000);
        modulator.setMinimum(50000);
        regiserMbean(mainQueueIn,"org.it4y.queue:type=input");
        regiserMbean(mainQueueOut,"org.it4y.queue:type=output");
        regiserMbean(bucket0,"org.it4y.queue:type=bucket0");
        regiserMbean(bucket1,"org.it4y.queue:type=bucket1");
        regiserMbean(bucket2,"org.it4y.queue:type=bucket2");
        regiserMbean(bucket3,"org.it4y.queue:type=bucket3");
        regiserMbean(bucket4,"org.it4y.queue:type=bucket4");
        regiserMbean(bucket5,"org.it4y.queue:type=bucket5");
        regiserMbean(staticLoad0,"org.it4y.load:type=load0");
        regiserMbean(staticLoad1,"org.it4y.load:type=load1");
        regiserMbean(staticLoad2,"org.it4y.load:type=load2");
        regiserMbean(staticLoad3,"org.it4y.load:type=load3");
        regiserMbean(staticLoad4,"org.it4y.load:type=load4");
        regiserMbean(staticLoad5,"org.it4y.load:type=load5");
        regiserMbean(sinker,"org.it4y.sinker:type=control");
        regiserMbean(modulator,"org.it4y.sinker:type=modulator");
        regiserMbean(quantumMonitor,"org.it4y.quantum:type=monitor");
        sinker.start();
        staticLoad0.start();
        staticLoad1.start();
        staticLoad2.start();
        staticLoad3.start();
        staticLoad4.start();
        staticLoad5.start();
    }

    public void run() {
        while(true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

}
