package load;

/**
 * Created by luc on 9/6/14.
 */
public interface LoadModulatorMBean {

    public double getMinimum();
    public void setMinimum(double min);
    public double getMaximum();
    public void setMaximum(double max);
    public long getStayLowTime();
    public void setStayLowTime(long msec);
    public long getStayHighTime();
    public void setStayHighTime(long msec);
    public int getTime();
    public void setTime(int t);
    public int getSamples();
    public void setSamples(int s);
    public double getDelta();
    public boolean isRunable();

    public void start();
    public void stop();
}
