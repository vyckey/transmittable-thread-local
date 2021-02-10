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
@file:JvmName("CreateTransmittableThreadLocalInstanceTps")

package com.alibaba.perf.tps

import com.alibaba.ttl.TransmittableThreadLocal
import com.alibaba.perf.getRandomString

/**
 * @author Jerry Lee (oldratlee at gmail dot com)
 */
fun main() {
    val tpsCounter = TpsCounter(2)

    tpsCounter.setAction {
        val threadLocal = TransmittableThreadLocal<String>()
        threadLocal.set(getRandomString())
    }

    while (true) {
        val start = tpsCounter.count
        Thread.sleep(1000)
        System.out.printf("tps: %d\n", tpsCounter.count - start)
    }
}
