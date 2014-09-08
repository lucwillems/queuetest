package net;

/**
 * Created by luc on 9/3/14.
 */
public interface Queue<T> {
    public boolean isEmpty();
    public long backlog();
    public long queued();
    public long dequeued();
    public long dropped();
    public int size();
    public T head();
    public boolean Queue(T x);
    public T deQueue();
    public void reset();
    public IntMetrics getDelay();

}
