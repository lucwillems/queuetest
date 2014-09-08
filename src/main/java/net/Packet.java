package net;

/**
 * Created by luc on 9/2/14.
 */
public class Packet {
    public int size;
    public int prio;
    public int id;
    public long timeStamp;

    public Packet(int size,int prio,int id) {
        this.size=size;
        this.prio=prio;
        this.id=id;
        this.timeStamp=System.currentTimeMillis();
    }

    public String toString() {
        StringBuilder stringBuilder=new StringBuilder(128);
        stringBuilder.append("s=").append(size).append(" ");
        stringBuilder.append("p=").append(prio).append(" ");
        stringBuilder.append("id=").append(id);
        return stringBuilder.toString();
    }
}
