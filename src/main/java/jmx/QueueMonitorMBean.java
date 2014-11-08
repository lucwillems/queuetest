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
public interface QueueMonitorMBean {
    public long getBackLog();
    public int getPackets();
    public long getRate();
    public void reset();
    public long getDropped();
    public int getMinDelay();
    public int getMeanDelay();
    public int getMaxDelay();
    public int getAvgDelay();
}
