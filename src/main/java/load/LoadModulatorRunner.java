package load;

import org.slf4j.LoggerFactory;

/**
 * Created by luc on 9/6/14.
 */
public class LoadModulatorRunner implements Runnable {

    private final LoadModulatorMBean input;
    private final SinkerMBean sinker;
    private double currentRate;
    private double deltaRate;
    private boolean up;
    private org.slf4j.Logger log= LoggerFactory.getLogger(LoadModulatorRunner.class);

    public LoadModulatorRunner(SinkerMBean sinker,LoadModulatorMBean input) {
        this.input=input;
        this.sinker=sinker;

    }
    @Override
    public void run() {
        currentRate=sinker.getRate();
        up=true;
        while(input.isRunable()) {
            if (currentRate>0) {
                log.debug("current rate: {}",currentRate);
                sinker.setRate(currentRate);
            }
            try {
                Thread.sleep(input.getTime());
            } catch (InterruptedException ignore) { return;}
            if (up) {
                if (currentRate<input.getMaximum()) {
                    currentRate+=input.getDelta();
                } else {
                    log.info("got maximum, waiting {} msec",input.getStayHighTime());
                    up=false;
                    try {
                        Thread.sleep(input.getStayHighTime());
                    } catch (InterruptedException ignore) {return;}
                    log.info("running to down...");
                }
            } else {
                if (currentRate > input.getMinimum()){
                    currentRate -=input.getDelta();
                } else {
                    log.info("got minimum, waiting {} msec",input.getStayLowTime());
                    up=true;
                    try {
                        Thread.sleep(input.getStayLowTime());
                    } catch (InterruptedException ignore) {return;}
                    log.info("running to up...");
                }
            }
        }
    }
}
