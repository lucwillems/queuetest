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

import com.google.common.util.concurrent.RateLimiter;
import net.Queue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Created by luc on 9/3/14.
 */
public class loadGeneratorRunner implements Runnable {
    private final Logger logger = LoggerFactory.getLogger(loadGeneratorRunner.class);
    private final RateLimiter limiter;
    private int id;
    private double prevRate;
    private final Random random=new Random();
    private final Queue<net.Packet> queue;
    private final LoadGeneratorMBean input;


    public loadGeneratorRunner(Queue<net.Packet> queue, LoadGeneratorMBean input) {
        this.limiter = RateLimiter.create(input.getRate(),1, TimeUnit.MICROSECONDS);
        this.queue = queue;
        this.input = input;
        this.prevRate = input.getRate();
    }

    @Override
    public void run() {
        Thread.currentThread().setName("load"+input.getPriority()+"-"+Thread.currentThread().getId());

        try {
            logger.info("starting {}", this);
            logger.info("rate: {}", input.getRate());
            limiter.setRate(input.getRate());
            while (input.isRunable()) {
                id++;
                logger.debug("send packet: {} prio:{}  size:{}", id, input.getPriority(), input.getPacketSize());
                queue.Queue(new net.Packet(input.getPacketSize(), input.getPriority(), id));
                int size=input.getPacketSize();
                //randomize some packet sizes delay
                if (input.getRandom()>0){
                    size=size+random.nextInt(input.getRandom());
                }
                limiter.acquire(size);
                if (input.getRate() != prevRate) {
                    logger.info("update rate to {}", input.getRate());
                    limiter.setRate(input.getRate());
                    prevRate = input.getRate();
                }
            }
        } catch (Throwable t) {
            logger.error("oeps load:", t);
        }
    }
}
