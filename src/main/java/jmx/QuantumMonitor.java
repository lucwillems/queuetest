
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

package jmx;

/**
 * Created by luc on 9/4/14.
 */
public class QuantumMonitor implements QuantumMonitorMBean {
    private long count;
    private long prevTime;
    private long prevCount;
    private long rate;

    public QuantumMonitor(){
        prevTime=System.currentTimeMillis();
    }

    public void inc() {
        count++;
    }

    @Override
    public long getRate() {
        long time=System.currentTimeMillis();
        long dtime=time-prevTime;
        if (dtime>=1000){
            long dcount=count-prevCount;
            if (dcount>=0){
                rate=(dcount*1000/dtime);
            }
            prevCount=count;
            prevTime=time;
        }
        return rate;
    }

}
