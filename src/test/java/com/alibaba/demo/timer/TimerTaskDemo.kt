/*
 * Copyright 2013 The TransmittableThreadLocal(TTL) Project
 *
 * The TTL Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.alibaba.demo.timer

import java.text.SimpleDateFormat
import java.util.*

/**
 * @see [Java Timer TimerTask Example](https://www.journaldev.com/1050/java-timer-timertask-example)
 */
fun main() {
    val timerTask = MyTimerTask()

    // running timer task as daemon thread
    val timer = Timer(true)
    timer.scheduleAtFixedRate(timerTask, 0, 300)
    println("TimerTask scheduled")

    // cancel after sometime
    Thread.sleep(1_000)
    timer.cancel()
    println("TimerTask cancelled")

    Thread.sleep(300)
}

class MyTimerTask : TimerTask() {
    override fun run() {
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
        println("Timer task started at: ${format.format(Date())}")
        Thread.sleep(200)
        println("Timer task finished at: ${format.format(Date())}")
    }

}
