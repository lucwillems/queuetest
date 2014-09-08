package net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by luc on 9/2/14.
 */
public class PrioBucketQueue implements Queue<Packet> {
    private Logger log = LoggerFactory.getLogger(PrioBucketQueue.class);
    private BucketQueue[] buckets;
    private int defaultPrio;
    private int pktCnt = 0;
    private Lock lock;
    private Condition notEmpty;
    private long backlog;
    private long queued;
    private long dequeue;
    private long dropped;
    private DeltaQueue DinQ=new DeltaQueue();
    private DeltaQueue DoutQ=new DeltaQueue();
    private IntMetrics delay;

    public PrioBucketQueue(int nrPrio, int defaultPrio) {
        assert defaultPrio < nrPrio && defaultPrio >= 0;
        this.buckets = new BucketQueue[nrPrio];
        this.defaultPrio = defaultPrio;
        this.lock = new ReentrantLock(true);
        this.notEmpty = lock.newCondition();
        this.delay=new IntMetrics(100);
    }


    public void init(BucketEvents eventListener) {
        for (int i = 0; i < buckets.length; i++) {
            buckets[i] = new BucketQueue(i, eventListener);
            if (eventListener != null) {
                eventListener.onInit(buckets[i]);
            }
        }
    }

    public BucketQueue getBucket(int i) {
        return buckets[i];

    }

    public void setup(final int[] x, int quatum) {
        //we need to make sum(x)=100%
        assert x.length == buckets.length;
        int total = 0;
        for (int i : x) {
            total = total + i;
        }
        //calculate 100% quatum
        int totalQuatum = quatum * 100;
        //now devide this per % to each bucket
        for (int i = 0; i < x.length; i++) {
            int bucketQ = totalQuatum * x[i] / total;
            buckets[i].setQuatum(bucketQ);
        }
    }

    @Override
    public boolean isEmpty() {
        return pktCnt == 0;
    }

    @Override
    public long backlog() {
        return backlog;
    }

    @Override
    public long queued() {
        return queued;
    }

    @Override
    public long dequeued() {
        return dequeue;
    }

    @Override
    public long dropped() {
        return dropped;
    }

    @Override
    public int size() {
        return pktCnt;
    }

    @Override
    public Packet head() {
        return null;
    }

    public boolean Queue(Packet p) {
        //todo : change this
        lock.lock();
        try {
            boolean queueResult = false;
            if (p.prio <0 || p.prio >=buckets.length ) {
                p.prio=defaultPrio;
            }
            DinQ.initOperation(buckets[p.prio]);
            try {
              if (! buckets[p.prio].Queue(p)) {
                    dropped += p.size;
              }
            } finally {
              DinQ.doneOperation(buckets[p.prio]);
              queued += p.size;
              backlog += DinQ.getDeltaBacklog();
              pktCnt += DinQ.getDeltaPktCnt();
            }
            notEmpty.signalAll();
        } finally {
            lock.unlock();
        }
        return true;
    }

    private void quantumize() {
        //seems all guaranteed buckets are used and we can not borrow anymore, lets quatumize all buckets
        //again because this means all queues have done there best but there still some packets in the queue
        for (BucketQueue bucket : buckets) {
            bucket.quatumize();
        }
    }

    @Override
    public Packet deQueue() {
        lock.lock();
        try {
            //incase we are empty we wait until 1 packet is queued
            while (pktCnt == 0) {
                try {
                    notEmpty.await();
                } catch (InterruptedException i) {
                    return null;
                }
            }
        } finally {
            lock.unlock();
        }
        while (pktCnt > 0) {
            //Try normal quatum based dequeing
            int allAllocatedcnt = 0;
            int backlogcnt=0;
            int quatumcnt=0;
            for (int i = 0; i < buckets.length; i++) {
                if (!buckets[i].isEmpty()) {
                    if (buckets[i].hasData()) {
                        lock.lock();
                        try {
                            //As queues like codel can drop multiple packets,
                            //we need to see and take care to have our counters correct
                            //this must be done in a lock so bucket queue can not changes
                            //except by changes done by the queue
                            DoutQ.initOperation(buckets[i]);
                            try {
                                Packet p = buckets[i].deQueue();
                                if (p !=null) {
                                    dequeue +=p.size;
                                    delay.add((int) (System.currentTimeMillis() - p.timeStamp));
                                    return p;
                                }
                           } finally {
                                DoutQ.doneOperation(buckets[i]);
                                backlog += DoutQ.getDeltaBacklog();
                                pktCnt  += DoutQ.getDeltaPktCnt();
                            }
                        } finally {
                            lock.unlock();
                        }
                    } else {
                        //count number of full buckets
                        allAllocatedcnt++;
                        if (!buckets[i].isEmpty()){
                            backlogcnt++;
                        }
                        if (buckets[i].hasQuantumUsed()){
                            quatumcnt++;
                        }
                    }
                    //Try borrowing from higher queues (if there willing to of course)
                    Packet head;
                    if ((head = buckets[i].head())!= null) {
                        //check if we can borrow from higher PRIO queues
                        for (int b = i; b >= 0; b--) {
                            if (buckets[b].canBorrow(head.size)) {
                                lock.lock();
                                try {
                                    //As queues like codel can drop multiple packets,
                                    //we need to see and take care to have our counters correct
                                    //this must be done in a lock so bucket queue can not changes
                                    //except by changes done by the queue
                                    DoutQ.initOperation(buckets[i]);
                                    try {
                                        Packet p = buckets[i].borrow();
                                        if (p !=null) {
                                            dequeue +=p.size;
                                            delay.add((int)(System.currentTimeMillis() - p.timeStamp));
                                            //account for the borrowed bytes we have dequeued in bucket[i]
                                            buckets[b].borrowBytes(p.size);
                                            return p;
                                        }
                                    } finally {
                                        DoutQ.doneOperation(buckets[i]);
                                        backlog += DoutQ.getDeltaBacklog();
                                        pktCnt  += DoutQ.getDeltaPktCnt();
                                    }
                                } finally {
                                    lock.unlock();
                                }
                            }
                        }
                    }
                }
            }
            if (allAllocatedcnt==backlogcnt && allAllocatedcnt>0) {
                    log.info("full: {} backlog: {} packetCnt: {} packetBacklog: {}", allAllocatedcnt, backlogcnt, pktCnt,backlog);
                    quantumize();
            }
        }
        return null;
    }

    @Override
    public void reset() {
        lock.lock();
        try {
            for (BucketQueue bucket : buckets) {
                bucket.reset();
            }
            backlog = 0;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public IntMetrics getDelay() {
        return delay;
    }
}
