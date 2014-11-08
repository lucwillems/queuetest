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
