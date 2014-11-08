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

import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by luc on 9/6/14.
 */
public class LoadModulator implements LoadModulatorMBean {
    private double originalRate;
    private final SinkerMBean sinker;
    private double minRate=10000;
    private double maxRate=20000;
    private int time=2000;
    private boolean runable;
    private final ScheduledExecutorService executor;
    private final org.slf4j.Logger log = LoggerFactory.getLogger(LoadModulator.class);
    private int samples=10;
    private long lowTime=8000;
    private long highTime=12000;

    public LoadModulator(ScheduledExecutorService executor,SinkerMBean sinker) {
        this.executor=executor;
        this.sinker=sinker;
    }

    @Override
    public double getMinimum() {
        return minRate;
    }

    @Override
    public void setMinimum(double min) {
        minRate=min;
    }

    @Override
    public double getMaximum() {
        return maxRate;
    }

    @Override
    public void setMaximum(double max) {
        maxRate=max;
    }

    @Override
    public long getStayLowTime() {
        return lowTime;
    }

    @Override
    public void setStayLowTime(long msec) {
        this.lowTime=msec;
    }

    @Override
    public long getStayHighTime() {
        return highTime;
    }

    @Override
    public void setStayHighTime(long msec) {
        this.highTime=msec;
    }

    @Override
    public int getTime() {
        return time;
    }

    @Override
    public void setTime(int t) {
        time=t;
    }

    @Override
    public int getSamples() {
        return samples;
    }

    @Override
    public void setSamples(int s) {
        this.samples=s;
    }

    @Override
    public double getDelta() {
        return (maxRate-minRate)/samples;
    }

    @Override
    public boolean isRunable() {
        return runable;
    }

    @Override
    public void start() {
        originalRate=sinker.getRate();
        log.info("Start load modulator on sinker");
        runable=true;
        executor.submit(new LoadModulatorRunner(sinker, this));
    }

    @Override
    public void stop() {
        runable=false;
        sinker.setRate(originalRate);
    }
}
