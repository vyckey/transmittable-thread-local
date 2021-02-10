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
package com.alibaba.perf.tps

import org.junit.Assert.assertTrue
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong

/**
 * @author Jerry Lee (oldratlee at gmail dot com)
 */
class TpsCounter internal constructor(private val threadCount: Int) {
    private val executorService: ExecutorService = Executors.newFixedThreadPool(threadCount)

    private val counter = AtomicLong()

    @Volatile
    private var stopped = false

    val count: Long
        get() = counter.get()

    internal fun setAction(runnable: Runnable) {
        val r = {
            while (!stopped) {
                runnable.run()
                counter.incrementAndGet()
            }
        }
        for (i in 0 until threadCount) {
            executorService.execute(r)
        }
    }

    fun stop() {
        stopped = true

        executorService.shutdown()
        assertTrue("Fail to shutdown thread pool", executorService.awaitTermination(100, TimeUnit.MILLISECONDS))
    }
}
