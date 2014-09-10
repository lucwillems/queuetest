import net.*;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrioQueuesTest {
    private final Logger logger= LoggerFactory.getLogger(PrioQueuesTest.class);

    @Test
    public void testPrioQueue1(){
        PrioBucketQueue queue=new PrioBucketQueue(7,6);
        queue.init(new BucketEvents() {
            @Override
            public void onInit(BucketQueue bucket) {
                bucket.setQueue(new simplePacketQueue());
            }

            @Override
            public void onQuantumized(BucketQueue bucket) {

            }

            @Override
            public void onBorrow(BucketQueue bucket, int size) {

            }
        });
        Assert.assertNotNull(queue);
        int[] procents={5,10,50,25,6,2,2};//sum must be 100%
        int mtu=1500;
        queue.setup(procents, mtu);
        Assert.assertEquals(7500,queue.getBucket(0).getQuatum());
        Assert.assertEquals(15000,queue.getBucket(1).getQuatum());
        Assert.assertEquals(75000,queue.getBucket(2).getQuatum());
        Assert.assertEquals(37500,queue.getBucket(3).getQuatum());
        Assert.assertEquals(9000,queue.getBucket(4).getQuatum());
        Assert.assertEquals(3000,queue.getBucket(5).getQuatum());
        Assert.assertEquals(3000,queue.getBucket(6).getQuatum());
        //insert some packets into first queue , size > 7500
        for (int i=0;i<10;i++){
            queue.Queue(new Packet(1123,0,i));
        }
        //now Dequeue and print result
        Packet p;
        do  {
            p=queue.deQueue();
            if (p!=null) {
                logger.info("net.Packet: {}", p);
            }
        } while (p !=null);
    }

    @Test
    public void testPrioQueue2(){
        int prios=7;
        PrioBucketQueue queue=new PrioBucketQueue(7,6);
        queue.init(new BucketEvents() {
            @Override
            public void onInit(BucketQueue bucket) {
                bucket.setQueue(new simplePacketQueue());
            }

            @Override
            public void onQuantumized(BucketQueue bucket) {

            }

            @Override
            public void onBorrow(BucketQueue bucket, int size) {

            }
        });
        Assert.assertNotNull(queue);
        int[] procents={5,10,50,25,6,2,2};//sum must be 100%
        int mtu=1500;
        queue.setup(procents, mtu);
        Assert.assertEquals(7500,queue.getBucket(0).getQuatum());
        Assert.assertEquals(15000,queue.getBucket(1).getQuatum());
        Assert.assertEquals(75000,queue.getBucket(2).getQuatum());
        Assert.assertEquals(37500,queue.getBucket(3).getQuatum());
        Assert.assertEquals(9000,queue.getBucket(4).getQuatum());
        Assert.assertEquals(3000,queue.getBucket(5).getQuatum());
        Assert.assertEquals(3000,queue.getBucket(6).getQuatum());
        //insert some packets into first queue , size > 7500
        for (int i=0;i<1000;i++){
            queue.Queue(new Packet(1123,2,i));
        }
        //now Dequeue and print result
        Packet p;
        do  {
            p=queue.deQueue();
            if (p!=null) {
                logger.info("net.Packet: {}", p);
            }
        } while (p !=null);
    }

    @Test
    public void testPrioQueue3(){
        int prios=7;
        PrioBucketQueue queue=new PrioBucketQueue(7,6);
        queue.init(new BucketEvents() {
            @Override
            public void onInit(BucketQueue bucket) {
                bucket.setQueue(new simplePacketQueue());
            }

            @Override
            public void onQuantumized(BucketQueue bucket) {

            }

            @Override
            public void onBorrow(BucketQueue bucket, int size) {

            }
        });
        Assert.assertNotNull(queue);
        int[] procents={5,10,50,25,6,2,2};//sum must be 100%
        int mtu=1500;
        queue.setup(procents, mtu);
        Assert.assertEquals(7500,queue.getBucket(0).getQuatum());
        Assert.assertEquals(15000,queue.getBucket(1).getQuatum());
        Assert.assertEquals(75000,queue.getBucket(2).getQuatum());
        Assert.assertEquals(37500,queue.getBucket(3).getQuatum());
        Assert.assertEquals(9000,queue.getBucket(4).getQuatum());
        Assert.assertEquals(3000,queue.getBucket(5).getQuatum());
        Assert.assertEquals(3000,queue.getBucket(6).getQuatum());
        //insert some packets into first queue , size > 7500
        for (int i=0;i<1000;i++){
            queue.Queue(new Packet(1123,6,i));
        }
        //now Dequeue and print result
        Packet p;
        do  {
            p=queue.deQueue();
            if (p!=null) {
                logger.info("net.Packet: {}", p);
            }
        } while (p !=null);
    }


    @Test
    public void testPrioQueuePrio1(){
        int prios=7;
        PrioBucketQueue queue=new PrioBucketQueue(7,6);
        queue.init(new BucketEvents() {
            @Override
            public void onInit(BucketQueue bucket) {
                bucket.setQueue(new simplePacketQueue());
            }

            @Override
            public void onQuantumized(BucketQueue bucket) {
                logger.info("bucket: {}",bucket);
            }

            @Override
            public void onBorrow(BucketQueue bucket, int size) {
                logger.info("borrow: {}",bucket);
            }
        });
        Assert.assertNotNull(queue);
        int[] procents={5,10,50,25,6,2,2};//sum must be 100%
        int mtu=1500;
        queue.setup(procents, mtu);
        Assert.assertEquals(7500,queue.getBucket(0).getQuatum());
        Assert.assertEquals(15000,queue.getBucket(1).getQuatum());
        Assert.assertEquals(75000,queue.getBucket(2).getQuatum());
        Assert.assertEquals(37500,queue.getBucket(3).getQuatum());
        Assert.assertEquals(9000,queue.getBucket(4).getQuatum());
        Assert.assertEquals(3000,queue.getBucket(5).getQuatum());
        Assert.assertEquals(3000,queue.getBucket(6).getQuatum());
        //insert some packets into first queue , size > 7500
        for (int i=0;i<1000;i++){
            queue.Queue(new Packet(1123,6,i));
        }
        //now Dequeue and print result
        Packet p;
        int i=0;
        while((p=queue.deQueue()) !=null) {
            logger.info("net.Packet: {}", p);
            if (i % 200 == 0) {
                logger.info("insert Hi prio");
                for (int j = 0; j < 20; j++) {
                    queue.Queue(new Packet(1500, 0, j));
                }
            }
            i++;
        }
    }

}