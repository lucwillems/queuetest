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

import net.BucketQueue;
import net.Queue;

/**
 * Created by luc on 9/4/14.
 */
public class BucketQueueMonitor extends QueueMonitor implements BucketQueueMonitorMBean {

    public BucketQueueMonitor(Queue<?> queue, boolean queued) {
        super(queue, queued);
    }

    @Override
    public int getAllocated() {
        return ((BucketQueue)queue).getAllocated();
    }

    @Override
    public int getBorrowed() {
        return ((BucketQueue)queue).getBorrowed();
    }

    @Override
    public int getLended() {
        return ((BucketQueue)queue).getLended();
    }

    @Override
    public int getQuantum() {
        return ((BucketQueue)queue).getQuatum();
    }
}
