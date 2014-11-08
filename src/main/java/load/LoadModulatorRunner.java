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

/**
 * Created by luc on 9/6/14.
 */
public class LoadModulatorRunner implements Runnable {

    private final LoadModulatorMBean input;
    private final SinkerMBean sinker;
    private final org.slf4j.Logger log= LoggerFactory.getLogger(LoadModulatorRunner.class);

    public LoadModulatorRunner(SinkerMBean sinker,LoadModulatorMBean input) {
        this.input=input;
        this.sinker=sinker;

    }
    @Override
    public void run() {
        double currentRate = sinker.getRate();
        boolean up = true;
        while(input.isRunable()) {
            if (currentRate >0) {
                log.debug("current rate: {}", currentRate);
                sinker.setRate(currentRate);
            }
            try {
                Thread.sleep(input.getTime());
            } catch (InterruptedException ignore) { return;}
            if (up) {
                if (currentRate <input.getMaximum()) {
                    currentRate +=input.getDelta();
                } else {
                    log.info("got maximum, waiting {} msec",input.getStayHighTime());
                    up =false;
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
                    up =true;
                    try {
                        Thread.sleep(input.getStayLowTime());
                    } catch (InterruptedException ignore) {return;}
                    log.info("running to up...");
                }
            }
        }
    }
}
