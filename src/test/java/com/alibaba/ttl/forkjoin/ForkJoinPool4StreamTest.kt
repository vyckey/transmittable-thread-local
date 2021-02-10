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
package com.alibaba.ttl.forkjoin

import com.alibaba.expandThreadPool
import com.alibaba.support.junit.conditional.ConditionalIgnoreRule
import com.alibaba.support.junit.conditional.ConditionalIgnoreRule.ConditionalIgnore
import com.alibaba.support.junit.conditional.IsAgentRunOrBelowJava8
import com.alibaba.support.junit.conditional.NoAgentRunOrBelowJava8
import com.alibaba.ttl.TransmittableThreadLocal
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.ForkJoinPool


private const val hello = "hello"

class ForkJoinPool4StreamTest {
    @Rule
    @JvmField
    val rule = ConditionalIgnoreRule()

    @Test
    @ConditionalIgnore(condition = NoAgentRunOrBelowJava8::class)
    fun test_stream_with_agent() {
        expandThreadPool(ForkJoinPool.commonPool())

        val ttl = TransmittableThreadLocal<String?>()
        ttl.set(hello)

        (0..100).map {
            ForkJoinPool.commonPool().submit {
                assertEquals(hello, ttl.get())
            }
        }.forEach { it.get() }

        (0..1000).toList().stream().parallel().mapToInt {
            assertEquals(hello, ttl.get())

            it
        }.sum().let {
            assertEquals((0..1000).sum(), it)
        }
    }

    @Test
    @ConditionalIgnore(condition = IsAgentRunOrBelowJava8::class)
    fun test_stream_no_agent() {
        val name = Thread.currentThread().name
        expandThreadPool(ForkJoinPool.commonPool())

        val ttl = TransmittableThreadLocal<String?>()
        ttl.set(hello)

        (0..100).map {
            ForkJoinPool.commonPool().submit {
                if (Thread.currentThread().name == name) assertEquals(hello, ttl.get())
                else assertNull(ttl.get())
            }
        }.forEach { it.get() }

        (0..1000).toList().stream().parallel().mapToInt {
            if (Thread.currentThread().name == name) assertEquals(hello, ttl.get())
            else assertNull(ttl.get())

            it
        }.sum().let {
            assertEquals((0..1000).sum(), it)
        }
    }
}
