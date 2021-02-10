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

import com.alibaba.support.junit.conditional.BelowJava7
import com.alibaba.support.junit.conditional.ConditionalIgnoreRule
import com.alibaba.support.junit.conditional.ConditionalIgnoreRule.ConditionalIgnore
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.ForkJoinPool
import java.util.concurrent.RecursiveTask
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger


class ForkJoinPoolTest {
    @Rule
    @JvmField
    val rule = ConditionalIgnoreRule()

    @Test
    @ConditionalIgnore(condition = BelowJava7::class)
    fun test_sameTaskDirectReturn_onlyExec1Time_ifHaveRun() {
        val pool = ForkJoinPool()

        val numbers = 1L..100L
        val sumTask = SumTask(numbers)

        // same task instance run 10 times
        for (i in 0..9) {
            assertEquals(numbers.sum(), pool.invoke(sumTask).toLong())
        }

        assertEquals(1, sumTask.execCounter.get().toLong())

        // close
        pool.shutdown()
        assertTrue("Fail to shutdown thread pool", pool.awaitTermination(100, TimeUnit.MILLISECONDS))
    }
}


internal class SumTask(private val numbers: LongRange) : RecursiveTask<Long>() {
    val execCounter = AtomicInteger(0)

    override fun compute(): Long {
        execCounter.incrementAndGet()

        return if (numbers.count() <= 16) {
            // compute directly
            numbers.sum()
        } else {
            // split task
            val middle = numbers.start + numbers.count() / 2

            val taskLeft = SumTask(numbers.start until middle)
            val taskRight = SumTask(middle..numbers.endInclusive)

            taskLeft.fork()
            taskRight.fork()
            taskLeft.join() + taskRight.join()
        }
    }
}
