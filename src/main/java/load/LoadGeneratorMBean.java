package load;

/**
 * Created by luc on 9/4/14.
 */
public interface LoadGeneratorMBean {
    public int getPriority();
    public int getPacketSize();
    public void setPacketSize(int PacketSize);
    public double  getRate();
    public void setRate(double rate);
    public boolean isRunable();
    public int getRandom();
    public void setRandom(int r);
    public void start();
    public void stop();

}
