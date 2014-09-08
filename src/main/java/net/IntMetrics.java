package net;

import java.util.Arrays;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by luc on 9/7/14.
 */
public class IntMetrics {
    private int[]data;
    private int ptr;
    private Lock lock=new ReentrantLock();
    private int min;
    private int max;
    private int mean;
    private long nextTime;
    private long lastTime;

    public IntMetrics(int size){
        data=new int[size];
        ptr=0;
        nextTime=System.currentTimeMillis()+1000;
    }

    public void add(int x) {
        lock.lock();
        try {
            if (ptr >= data.length) {
                System.arraycopy(data, 1, data, 0, data.length - 1);
            }
            data[ptr] = x;
            ptr=Math.min(ptr+1,data.length-1);
        } finally {
            lock.unlock();
        }
    }
    public int getMin() {
        checkUpdate();
        return min;
    }

    private void checkUpdate() {
        long time=System.currentTimeMillis();
        if (time>=nextTime) {
            long delta=time-lastTime;
            lock.lock();
            try{
                if (ptr>2) {
                    Arrays.sort(data, 0, ptr);
                    min = data[0];
                    max = data[ptr-1];
                    mean = data[ptr * 66 / 100];
                    ptr = 0;
                } else {
                    min=data[0];
                    max=data[Math.max(0,ptr-1)];
                    mean=data[Math.max(0,ptr-1)];
                }
            } finally {
                lock.unlock();
            }
        }
    }

    public int getMax() {
        checkUpdate();
        return max;
    }

    public int getMean() {
        checkUpdate();
        return mean;
    }

}
