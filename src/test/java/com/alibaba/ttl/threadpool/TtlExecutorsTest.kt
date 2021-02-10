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
package com.alibaba.ttl.threadpool

import com.alibaba.noTtlAgentRun
import com.alibaba.ttl.TtlCallable
import com.alibaba.ttl.TtlRunnable
import com.alibaba.ttl.TtlUnwrap
import com.alibaba.ttl.threadpool.TtlExecutors.*
import org.junit.Assert.*
import org.junit.Test
import java.util.concurrent.Executor
import java.util.concurrent.Executors.newScheduledThreadPool
import java.util.concurrent.ThreadFactory
import java.util.concurrent.TimeUnit

/**
 * @author Jerry Lee (oldratlee at gmail dot com)
 */
class TtlExecutorsTest {

    @Test
    fun test_common() {
        val newScheduledThreadPool = newScheduledThreadPool(3)

        getTtlExecutor(newScheduledThreadPool).let {
            if (noTtlAgentRun()) assertTrue(it is ExecutorTtlWrapper)
            assertEquals(noTtlAgentRun(), isTtlWrapper(it))

            assertSame(newScheduledThreadPool, unwrap(it))
            assertSame(newScheduledThreadPool, TtlUnwrap.unwrap(it))
        }
        getTtlExecutorService(newScheduledThreadPool).let {
            if (noTtlAgentRun()) assertTrue(it is ExecutorServiceTtlWrapper)
            assertEquals(noTtlAgentRun(), isTtlWrapper(it))

            assertSame(newScheduledThreadPool, unwrap(it))
            assertSame(newScheduledThreadPool, TtlUnwrap.unwrap(it))
        }
        getTtlScheduledExecutorService(newScheduledThreadPool).let {
            if (noTtlAgentRun()) assertTrue(it is ScheduledExecutorServiceTtlWrapper)
            assertEquals(noTtlAgentRun(), isTtlWrapper(it))

            assertSame(newScheduledThreadPool, unwrap(it))
            assertSame(newScheduledThreadPool, TtlUnwrap.unwrap(it))
        }

        val threadFactory = ThreadFactory { Thread(it) }
        getDisableInheritableThreadFactory(threadFactory).let {
            assertTrue(it is DisableInheritableThreadFactory)
            assertTrue(isDisableInheritableThreadFactory(it))

            assertSame(threadFactory, unwrap(it))
            assertSame(threadFactory, TtlUnwrap.unwrap(it))
        }

        newScheduledThreadPool.shutdown()
    }

    @Test
    fun test_null() {
        assertNull(getTtlExecutor(null))
        assertNull(getTtlExecutorService(null))
        assertNull(getTtlScheduledExecutorService(null))

        assertFalse(isTtlWrapper(null))
        assertNull(unwrap<Executor>(null))
    }

    @Test
    fun test_is_idempotent() {
        val newScheduledThreadPool = newScheduledThreadPool(3)

        getTtlExecutor(newScheduledThreadPool)?.let {
            it.execute(TtlRunnable.get { }!!)
        }

        getTtlExecutorService(newScheduledThreadPool)?.let {
            it.submit(TtlCallable.get { 42 }!!).get()
            it.submit(TtlRunnable.get { }!!, 42).get()
            it.submit(TtlRunnable.get { }!!).get()

            it.invokeAll(listOf(TtlCallable.get { 42 }!!)).map { f -> f.get() }
            it.invokeAll(listOf(TtlCallable.get { 42 }!!), 1, TimeUnit.SECONDS).map { f -> f.get() }

            it.invokeAny(listOf(TtlCallable.get { 42 }!!))
            it.invokeAny(listOf(TtlCallable.get { 42 }!!), 1, TimeUnit.SECONDS)
        }

        getTtlScheduledExecutorService(newScheduledThreadPool)?.let {
            it.schedule(TtlRunnable.get { }!!, 1, TimeUnit.MICROSECONDS).get()
            it.schedule(TtlCallable.get { 42 }!!, 1, TimeUnit.MICROSECONDS).get()

            it.scheduleAtFixedRate(TtlRunnable.get { }!!, 0, 1, TimeUnit.MICROSECONDS).cancel(true)
            it.scheduleWithFixedDelay(TtlRunnable.get { }!!, 0, 1, TimeUnit.MICROSECONDS).cancel(true)
        }

        newScheduledThreadPool.shutdown()
    }
}
