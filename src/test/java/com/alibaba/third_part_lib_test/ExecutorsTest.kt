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
package com.alibaba.third_part_lib_test

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.concurrent.Executors
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit


class ExecutorsTest {
    @Test
    fun test_remove_of_ThreadPoolExecutor() {
        val size = 2
        val threadPool = Executors.newFixedThreadPool(size) as ThreadPoolExecutor

        val futures = (0..size * 2).map {
            threadPool.submit {
                Thread.sleep(10)
            }
        }

        Runnable {
            println("Task should be removed!")
        }.let {
            threadPool.execute(it)
            assertTrue(threadPool.remove(it))
            assertFalse(threadPool.remove(it))
        }

        // wait sleep task finished.
        futures.forEach { it.get() }

        threadPool.shutdown()
        assertTrue("Fail to shutdown thread pool", threadPool.awaitTermination(100, TimeUnit.MILLISECONDS))
    }
}


