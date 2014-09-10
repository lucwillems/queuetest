package net;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by luc on 9/2/14.
 */
public class BucketQueue implements Queue<Packet> {
    private int quatum;
    private final BucketEvents notifier;
    private final int priority;
    private int allocated;
    private int borrowed;
    private int lended;
    private final Lock lock;
    private Queue<Packet> queue;
    private long backlog;
    private long queued;
    private long dequeue;
    private long dropped;
    private long dequeuepkt;
    private final DelayMetrics delay;

    public BucketQueue(int priority, BucketEvents notifier) {
        this.priority = priority;
        this.notifier = notifier;
        this.lock = new ReentrantLock();
        this.delay=new DelayMetrics(30,1000);
    }

    @Override
    public boolean isEmpty() {
        assert queue != null;
        return queue.isEmpty();
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
        assert queue != null;
        return queue.size();
    }

    @Override
    public Packet head() {
        assert queue != null;
        return queue.head();
    }

    @Override
    public boolean Queue(Packet x) {
        assert queue != null;
        lock.lock();
        try {
            //Queuing more than quatum doens't make sence
            //so drop them else we get in overload with memory
            if (backlog > quatum) {
                //Do head drop !!
                Packet p = queue.deQueue();
                if (p!=null) {
                    dropped += p.size;
                    backlog -= p.size;
                }
            }
            backlog += x.size;
            queued +=x.size;
            queue.Queue(x);
            return true;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void reset() {
        assert queue != null;
        queue.reset();
        borrowed=0;
        lended=0;
    }

    @Override
    public DelayMetrics getDelay() {
        return delay;
    }

    private Packet do_deQueue() {
        Packet p = queue.deQueue();
        dequeuepkt++;
        //for testing : add random drop/null packet results
        //we must keep our counters ok
//        if (dequeuepkt% 100 ==0){
//            log.info("random drop on bucket {} after {} pkts",priority,dequeuepkt);
//            backlog -= p.size;
//            dropped += p.size;
//            return null;
//        }
        return p;
    }

    public Packet deQueue() {
        assert queue != null;
        lock.lock();
        try {
            Packet p = do_deQueue();
            if(p!=null) {
                allocated = allocated + p.size;
                dequeue += p.size;
                backlog -= p.size;
                delay.add(p.delay());
            }
            return p;
        } finally {
            lock.unlock();
        }
    }

    protected Packet borrow() {
        lock.lock();
        try {
            Packet p = do_deQueue();
            if (p!=null) {
                borrowed = borrowed + p.size;
                dequeue += p.size;
                backlog -= p.size;
            }
            return p;
        } finally {
            lock.unlock();
        }
    }

    public boolean hasQuantumUsed() {
        return allocated >=quatum;
    }

    protected boolean hasData() {
        return queue.size() > 0 & (allocated-lended) < quatum;
    }

    protected boolean canBorrow(int x) {
        return queue.isEmpty() & (quatum - allocated) > x;
    }

    protected void borrowBytes(int x) {
        lock.lock();
        try {
            if (notifier != null) {
                notifier.onBorrow(this, x);
            }
            allocated = allocated + x;
            lended=lended+x;
        } finally {
            lock.unlock();
        }
    }

    protected void setQuatum(int q) {
        this.quatum = q;
    }

    public int getQuatum() {
        return quatum;
    }


    public void quatumize() {
        lock.lock();
        try {
            if (notifier != null) {
                notifier.onQuantumized(this);
            }
            if (allocated > 0) {
                allocated = Math.max(0, allocated - quatum);
                borrowed = 0;
                lended=0;
            }
        } finally {
            lock.unlock();
        }
    }

    public int getAllocated() {
        return allocated;
    }

    public int getBorrowed() {
        return borrowed;
    }
    public int getLended() {return lended;}

    public void setQueue(Queue<Packet> q) {
        this.queue = q;
    }

    public Queue<Packet> getQueue() {
        return queue;
    }

    public int getPriority() {
        return priority;
    }

    public String toString() {
        String x= allocated>quatum ? "*":"";
        return x+"alloc=" + allocated + " borrow=" + borrowed + " lended= "+lended+" quatum=" + quatum + " size=" + queue.size()+" backlog="+backlog;
    }
}
