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
@file:JvmName("NoMemoryLeak_TransmittableThreadLocal_NoRemove")

package com.alibaba.perf.memoryleak

import com.alibaba.ttl.TransmittableThreadLocal
import com.alibaba.perf.getRandomString

/**
 * @author Jerry Lee (oldratlee at gmail dot com)
 */
fun main() {
    var counter: Long = 0
    while (true) {
        val threadLocal = TransmittableThreadLocal<String>()
        threadLocal.set(getRandomString())

        if (counter % 1000 == 0L)
            System.out.printf("%05dK%n", counter / 1000)
        counter++
    }
}
