package load;

/**
 * Created by luc on 9/4/14.
 */
public interface SinkerMBean {
    public double  getRate();
    public void setRate(double rate);
    public boolean isRunable();
    public long getDelay();
    public void addDelay(long d);
    public void start();
    public void stop();

}
