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

import com.alibaba.hasTtlTtlAgentRunWithDisableInheritableForThreadPool
import com.alibaba.support.junit.conditional.BelowJava7
import com.alibaba.support.junit.conditional.ConditionalIgnoreRule
import com.alibaba.support.junit.conditional.ConditionalIgnoreRule.ConditionalIgnore
import com.alibaba.support.junit.conditional.IsAgentRun
import com.alibaba.support.junit.conditional.IsAgentRunOrBelowJava7
import com.alibaba.ttl.threadpool.TtlExecutors
import com.alibaba.ttl.threadpool.TtlForkJoinPoolHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.ForkJoinPool
import java.util.concurrent.ThreadPoolExecutor

private const val hello = "hello"
private val defaultValue = "${Date()} ${Math.random()}"

class InheritableTest {
    @Rule
    @JvmField
    val rule = ConditionalIgnoreRule()

    // ===================================================
    // Executors
    // ===================================================

    @Test
    fun inheritable_Executors() {
        val threadPool = Executors.newCachedThreadPool()
        try {
            val ttl = TransmittableThreadLocal<String?>()
            ttl.set(hello)

            val callable = Callable { ttl.get() } // NO TtlWrapper(TtlCallable) here!!

            // get "hello" value is transmitted by InheritableThreadLocal function!
            // NOTE: Executors.newCachedThreadPool create thread lazily
            assertEquals(hello, threadPool.submit(callable).get())

            // current thread's TTL must be exist
            assertEquals(hello, ttl.get())
        } finally {
            threadPool.shutdown()
        }
    }

    @Test
    @ConditionalIgnore(condition = IsAgentRun::class)
    fun disableInheritable_Executors_DisableInheritableThreadFactory() {
        val threadPool = Executors.newCachedThreadPool(TtlExecutors.getDefaultDisableInheritableThreadFactory())
        try {
            val ttl = TransmittableThreadLocal<String?>()
            ttl.set(hello)

            val callable = Callable { ttl.get() } // NO TtlWrapper(TtlCallable) here!!

            // when ttl agent is loaded, Callable is wrapped when submit,
            // so here value is "hello" transmitted by TtlCallable wrapper
            // IGNORE this test case when TtlAgent is run.
            assertNull(threadPool.submit(callable).get())

            // current thread's TTL must be exist when using DisableInheritableThreadFactory
            assertEquals(hello, ttl.get())
        } finally {
            threadPool.shutdown()
        }
    }

    @Test
    @ConditionalIgnore(condition = IsAgentRun::class)
    fun disableInheritable_Executors_TtlDisableInheritableWithInitialValue() {
        val threadPool = Executors.newCachedThreadPool()
        try {
            val ttl = object : TransmittableThreadLocal<String?>() {
                override fun childValue(parentValue: String?): String? = initialValue()
            }
            ttl.set(hello)

            val callable = Callable { ttl.get() } // NO TtlWrapper(TtlCallable) here!!

            // when ttl agent is loaded, Callable is wrapped when submit,
            // so here value is "hello" transmitted by TtlCallable wrapper
            // IGNORE this test case when TtlAgent is run.
            assertNull(threadPool.submit(callable).get())

            // current thread's TTL must be exist
            assertEquals(hello, ttl.get())
        } finally {
            threadPool.shutdown()
        }
    }

    @Test
    @ConditionalIgnore(condition = IsAgentRun::class)
    fun disableInheritable_Executors_TtlDefaultValue_TtlDisableInheritableWithInitialValue() {
        val threadPool = Executors.newCachedThreadPool()
        try {
            val ttl = object : TransmittableThreadLocal<String>() {
                override fun initialValue(): String = defaultValue
                override fun childValue(parentValue: String): String = initialValue()
            }
            ttl.set(hello)

            val callable = Callable { ttl.get() } // NO TtlWrapper(TtlCallable) here!!

            // when ttl agent is loaded, Callable is wrapped when submit,
            // so here value is "hello" transmitted by TtlCallable wrapper
            // IGNORE this test case when TtlAgent is run.
            assertEquals(defaultValue, threadPool.submit(callable).get())

            // current thread's TTL must be exist when using DisableInheritableThreadFactory
            assertEquals(hello, ttl.get())
        } finally {
            threadPool.shutdown()
        }
    }

    @Test
    @ConditionalIgnore(condition = IsAgentRun::class)
    fun disableInheritable_Executors_TtlDefaultValue_DisableInheritableThreadFactory_TtlWithInitialValue() {
        val threadPool = Executors.newCachedThreadPool(TtlExecutors.getDefaultDisableInheritableThreadFactory())
        try {
            val ttl = object : TransmittableThreadLocal<String>() {
                override fun initialValue(): String = defaultValue
                override fun childValue(parentValue: String): String = initialValue()
            }
            ttl.set(hello)

            val callable = Callable { ttl.get() } // NO TtlWrapper(TtlCallable) here!!

            // when ttl agent is loaded, Callable is wrapped when submit,
            // so here value is "hello" transmitted by TtlCallable wrapper
            // IGNORE this test case when TtlAgent is run.
            assertEquals(defaultValue, threadPool.submit(callable).get())

            // current thread's TTL must be exist when using DisableInheritableThreadFactory
            assertEquals(hello, ttl.get())
        } finally {
            threadPool.shutdown()
        }
    }

    @Test
    fun disableInheritable_Executors_ByAgent() {
        val threadPool = Executors.newCachedThreadPool() as ThreadPoolExecutor
        try {
            assertEquals(hasTtlTtlAgentRunWithDisableInheritableForThreadPool(),
                    TtlExecutors.isDisableInheritableThreadFactory(threadPool.threadFactory))
        } finally {
            threadPool.shutdown()
        }
    }

    // ===================================================
    // ForkJoinPool
    // ===================================================

    @Test
    @ConditionalIgnore(condition = BelowJava7::class)
    fun inheritable_ForkJoinPool() {
        val threadPool = ForkJoinPool(4)
        try {
            val ttl = TransmittableThreadLocal<String?>()
            ttl.set(hello)

            val callable = Callable { ttl.get() } // NO TtlWrapper(TtlCallable) here!!

            // get "hello" value is transmitted by InheritableThreadLocal function!
            // NOTE: Executors.newCachedThreadPool create thread lazily
            assertEquals(hello, threadPool.submit(callable).get())

            // current thread's TTL must be exist
            assertEquals(hello, ttl.get())
        } finally {
            threadPool.shutdown()
        }
    }

    @Test
    @ConditionalIgnore(condition = IsAgentRunOrBelowJava7::class)
    fun disableInheritable_ForkJoinPool_DisableInheritableForkJoinWorkerThreadFactory() {
        val threadPool = ForkJoinPool(4, TtlForkJoinPoolHelper.getDefaultDisableInheritableForkJoinWorkerThreadFactory(), null, false)
        try {
            val ttl = TransmittableThreadLocal<String?>()
            ttl.set(hello)

            val callable = Callable { ttl.get() } // NO TtlWrapper(TtlCallable) here!!

            // when ttl agent is loaded, Callable is wrapped when submit,
            // so here value is "hello" transmitted by TtlCallable wrapper
            // IGNORE this test case when TtlAgent is run.
            assertNull(threadPool.submit(callable).get())

            // current thread's TTL must be exist when using DisableInheritableForkJoinWorkerThreadFactory
            assertEquals(hello, ttl.get())
        } finally {
            threadPool.shutdown()
        }
    }

    @Test
    @ConditionalIgnore(condition = IsAgentRunOrBelowJava7::class)
    fun disableInheritable_ForkJoinPool_TtlDisableInheritableWithInitialValue() {
        val threadPool = ForkJoinPool(4)
        try {
            val ttl = object : TransmittableThreadLocal<String?>() {
                override fun childValue(parentValue: String?): String? = initialValue()
            }
            ttl.set(hello)

            val callable = Callable { ttl.get() } // NO TtlWrapper(TtlCallable) here!!

            // when ttl agent is loaded, Callable is wrapped when submit,
            // so here value is "hello" transmitted by TtlCallable wrapper
            // IGNORE this test case when TtlAgent is run.
            assertNull(threadPool.submit(callable).get())

            // current thread's TTL must be exist
            assertEquals(hello, ttl.get())
        } finally {
            threadPool.shutdown()
        }
    }

    @Test
    @ConditionalIgnore(condition = IsAgentRunOrBelowJava7::class)
    fun disableInheritable_ForkJoinPool_TtlDefaultValue_TtlDisableInheritableWithInitialValue() {
        val threadPool = ForkJoinPool(4)
        try {
            val ttl = object : TransmittableThreadLocal<String>() {
                override fun initialValue(): String = defaultValue
                override fun childValue(parentValue: String): String = initialValue()
            }
            ttl.set(hello)

            val callable = Callable { ttl.get() } // NO TtlWrapper(TtlCallable) here!!

            // when ttl agent is loaded, Callable is wrapped when submit,
            // so here value is "hello" transmitted by TtlCallable wrapper
            // IGNORE this test case when TtlAgent is run.
            assertEquals(defaultValue, threadPool.submit(callable).get())

            // current thread's TTL must be exist
            assertEquals(hello, ttl.get())
        } finally {
            threadPool.shutdown()
        }
    }

    @Test
    @ConditionalIgnore(condition = IsAgentRunOrBelowJava7::class)
    fun disableInheritable_ForkJoinPool_TtlDefaultValue_DisableInheritableForkJoinWorkerThreadFactory_TtlWithInitialValue() {
        val threadPool = ForkJoinPool(4, TtlForkJoinPoolHelper.getDefaultDisableInheritableForkJoinWorkerThreadFactory(), null, false)
        try {
            val ttl = object : TransmittableThreadLocal<String>() {
                override fun initialValue(): String = defaultValue
                override fun childValue(parentValue: String): String = initialValue()
            }
            ttl.set(hello)

            val callable = Callable { ttl.get() } // NO TtlWrapper(TtlCallable) here!!

            // when ttl agent is loaded, Callable is wrapped when submit,
            // so here value is "hello" transmitted by TtlCallable wrapper
            // IGNORE this test case when TtlAgent is run.
            assertEquals(defaultValue, threadPool.submit(callable).get())

            // current thread's TTL must be exist when using DisableInheritableForkJoinWorkerThreadFactory
            assertEquals(hello, ttl.get())
        } finally {
            threadPool.shutdown()
        }
    }

    @Test
    @ConditionalIgnore(condition = BelowJava7::class)
    fun disableInheritable_ForkJoinPool_ByAgent() {
        val threadPool = ForkJoinPool(4)
        try {
            assertEquals(hasTtlTtlAgentRunWithDisableInheritableForThreadPool(),
                    TtlForkJoinPoolHelper.isDisableInheritableForkJoinWorkerThreadFactory(threadPool.factory))
        } finally {
            threadPool.shutdown()
        }
    }

}
