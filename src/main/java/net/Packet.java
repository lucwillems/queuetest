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

package net;

/**
 * Created by luc on 9/2/14.
 */
public class Packet {
    public final int size;
    public int prio;
    public final int id;
    private final long timeStamp;

    public Packet(int size,int prio,int id) {
        this.size=size;
        this.prio=prio;
        this.id=id;
        this.timeStamp=Clock.getPacketTimeStamp();
    }

    /* NOTE : this can become negative because of the fact that nanoTime() is not multi core save and unsign integer
     * wraps from =Long.MAX_VALUE/1000 to 0
     *
     * ANYONE using this time must do following :
     * - only use for delta time measurements
     * - ignore < 0 values
     */
    public int delay() {
        long current=Clock.getPacketTimeStamp();
        return (int)(current-timeStamp);
    }

    public String toString() {
        return "s=" + size + " " + "p=" + prio + " " + "id=" + id;
    }
}
