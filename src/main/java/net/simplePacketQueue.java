package net;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by luc onv 9/3/14.
 */
public class simplePacketQueue implements net.Queue<Packet> {
    protected java.util.Queue<Packet> queue;
    protected Lock lock;
    private int maxPkt=Integer.MAX_VALUE;
    private long backlog;
    private long queued;
    private long dequeue;
    private long lastDelay;
    private IntMetrics delay;

    public simplePacketQueue() {
        queue=new LinkedList<Packet>();//todo replace this by codel
        lock=new ReentrantLock();
        delay=new IntMetrics(100);
    }

    public void setMaxPkt(int max) {
        this.maxPkt=max;
    }
    @Override
    public boolean isEmpty() {
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
        return 0;
    }

    @Override
    public int size() {
        return queue.size();
    }

    @Override
    public Packet head() {
        return queue.peek();
    }

    @Override
    public boolean Queue(Packet x) {
        lock.lock();
        try {
            queue.add(x);
            queued+=x.size;
            backlog+=x.size;
        } finally {
            lock.unlock();
        }
        return true;
    }

    @Override
    public Packet deQueue() {
        lock.lock();
        try {
            Packet p=queue.remove();
            if (p!=null) {
                queued-=p.size;
                backlog-=p.size;
                delay.add((int) (System.currentTimeMillis()-p.timeStamp));
            }
            return p;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void reset() {
        lock.lock();
        try {
            queue.clear();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public IntMetrics getDelay() {
        return delay;
    }
}
