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
package com.alibaba.ttl

import org.junit.Assert.*
import org.junit.Test
import java.util.*

@Suppress("DEPRECATION")
class TtlTimerTaskTest {
    @Test
    fun test_get() {
        assertNull(TtlTimerTask.get(null))

        val timerTask = object : TimerTask() {
            override fun run() {}
        }

        val ttlTimerTask = TtlTimerTask.get(timerTask)
        assertTrue(ttlTimerTask is TtlTimerTask)
    }

    @Test
    fun test_unwrap() {
        assertNull(TtlTimerTask.unwrap(null))

        val timerTask = object : TimerTask() {
            override fun run() {}
        }
        val ttlTimerTask = TtlTimerTask.get(timerTask)


        assertSame(timerTask, TtlTimerTask.unwrap(timerTask))
        assertSame(timerTask, TtlTimerTask.unwrap(ttlTimerTask))


        assertEquals(listOf(timerTask), TtlTimerTask.unwraps(listOf(timerTask)))
        assertEquals(listOf(timerTask), TtlTimerTask.unwraps(listOf(ttlTimerTask)))
        assertEquals(listOf(timerTask, timerTask), TtlTimerTask.unwraps(listOf(ttlTimerTask, timerTask)))
        assertEquals(listOf<TimerTask>(), TtlTimerTask.unwraps(null))
    }
}
