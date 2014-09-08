package load;

/**
 * Created by luc on 9/3/14.
 */
public class LoadTestMain {
    static LoadTestApp app;

    public static void main(String[] args) {
        app=new LoadTestApp();
        app.init();
        app.run();
    }
}
