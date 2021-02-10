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
package com.alibaba.demo.scheduled_thread_pool_executor

import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 * ScheduledThreadPoolExecutor usage demo for Issue 148
 * https://github.com/alibaba/transmittable-thread-local/issues/148
 */
fun main() {
    val scheduledThreadPoolExecutor = ScheduledThreadPoolExecutor(10)

    val task = Runnable { println("I'm a Runnable task, I'm working...") }
    val scheduledFuture = scheduledThreadPoolExecutor.scheduleWithFixedDelay(task, 500, 500, TimeUnit.MILLISECONDS)

    Thread.sleep(2_000)

    println("cancel")
    val cancelResult = scheduledFuture.cancel(false)
    println("canceled: $cancelResult")  // scheduled task cancel success!

    Thread.sleep(2_000)
    scheduledThreadPoolExecutor.shutdown()
    println("Bye")
}
