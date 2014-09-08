package net;

/**
 * Created by luc on 9/7/14.
 */
public class DeltaQueue {
    private long backlog;
    private int pktcnt;
    private long dbacklog;
    private int dpktcnt;

    public void initOperation(Queue<Packet> queue) {
        backlog=queue.backlog();
        pktcnt=queue.size();
    }
    public void doneOperation(Queue<Packet> queue) {
        dbacklog=queue.backlog()-backlog;
        dpktcnt=queue.size()-pktcnt;
    }

    public long getDeltaBacklog() {
        return dbacklog;
    }
    public int getDeltaPktCnt() {
        return dpktcnt;
    }
}
