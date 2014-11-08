/*
 * Copyright 2014 Luc Willems (T.M.M.)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package load;

import net.Packet;
import net.Queue;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by luc on 9/4/14.
 */
public class LoadGenerator implements LoadGeneratorMBean {
    private final org.slf4j.Logger log= LoggerFactory.getLogger(LoadGenerator.class);
    private int packetSize=1000;
    private double rate=100000;
    private boolean runnable=false;
    private final int prio;
    final ScheduledExecutorService executor;
    private final Queue<Packet> queue;
    private int random;

    public LoadGenerator(ScheduledExecutorService executor,Queue<Packet> queue, int p) {
        this.executor=executor;
        this.queue=queue;
        this.prio=p;
    }

    @Override
    public int getPriority() {
        return prio;
    }

    @Override
    public int getPacketSize() {
        return packetSize;
    }

    @Override
    public void setPacketSize(int s) {
        log.info("set packet size: {}",s);
        this.packetSize=s;
    }

    @Override
    public double getRate() {
        return rate;
    }

    @Override
    public void setRate(double r) {
        log.info("set rate: {}",r);
        this.rate=r;
    }

    @Override
    public void start() {
        runnable=true;
        log.info("Start load generator "+prio);
        executor.submit(new loadGeneratorRunner(queue, this));
        //todo : keep track and handle futures
    }

    @Override
    public void stop() {
        log.info("Stop load generator "+prio);
        runnable=false;
    }

    public boolean isRunable() {
        return runnable;
    }

    @Override
    public int getRandom() {
        return random;
    }

    @Override
    public void setRandom(int r) {
        random=r;
    }
}
