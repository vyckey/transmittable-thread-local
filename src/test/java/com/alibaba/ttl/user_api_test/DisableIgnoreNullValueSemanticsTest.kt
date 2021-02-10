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
package com.alibaba.ttl.user_api_test

import com.alibaba.ttl.TransmittableThreadLocal
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.util.concurrent.FutureTask
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.thread

/**
 * Test the "Ignore-Null-Value Semantics" of [TransmittableThreadLocal] from user code(different package)
 */
class DisableIgnoreNullValueSemanticsTest {
    @Test
    fun test_TTL_not_disableIgnoreNullValueSemantics_defaultTtlBehavior() {
        val ttl = object : TransmittableThreadLocal<String?>() {
            override fun initialValue(): String {
                return "init"
            }

            override fun childValue(parentValue: String?): String {
                return "$parentValue + child"
            }
        }

        assertEquals("init", ttl.get())
        ttl.set(null)
        // DO NOT `ttl.get()` !
        //   `get` operation will re-init the value of ThreadLocal

        val task = FutureTask {
            ttl.get()
        }
        thread { task.run() }.join()

        // `get` operation will re-init the value of ThreadLocal !
        assertEquals("init", ttl.get())
        // "Ignore-Null-Value Semantics" will not transmit ThreadLocal with the null value,
        // so the value in new thread is "init" value
        assertEquals("init", task.get())

        //////////////////////////////////////

        val task2 = FutureTask {
            ttl.get()
        }
        thread { task2.run() }.join()

        assertEquals("init", ttl.get())
        assertEquals("init + child", task2.get())
    }

    @Test
    fun test_TTL_not_disableIgnoreNullValueSemantics_defaultTtlBehavior_getSafe_ForNullInit() {
        val count = AtomicInteger()

        val ttl = object : TransmittableThreadLocal<String?>() {
            override fun initialValue(): String? {
                count.getAndIncrement()
                return super.initialValue()
            }

            override fun childValue(parentValue: String?): String? {
                count.getAndSet(1000)
                return super.childValue(parentValue)
            }
        }

        assertNull(ttl.get())
        assertEquals(1, count.get())

        ttl.set(null)
        assertNull(ttl.get())
        assertEquals(2, count.get())
    }

    @Test
    fun test_TTL_disableIgnoreNullValueSemantics_sameAsThreadLocal() {
        val ttl = object : TransmittableThreadLocal<String?>(true) {
            override fun initialValue(): String {
                return "init"
            }

            override fun childValue(parentValue: String?): String {
                return "$parentValue + child"
            }
        }

        assertEquals("init", ttl.get())
        ttl.set(null)
        assertNull(ttl.get())

        val task = FutureTask {
            ttl.get()
        }
        thread { task.run() }.join()

        assertNull(ttl.get())
        assertEquals("null + child", task.get())

        //////////////////////////////////////

        val task2 = FutureTask {
            ttl.get()
        }
        thread { task2.run() }.join()

        assertNull(ttl.get())
        assertEquals("null + child", task.get())
    }

    @Test
    fun test_InheritableThreadLocal() {
        val ttl = object : InheritableThreadLocal<String?>() {
            override fun initialValue(): String {
                return "init"
            }

            override fun childValue(parentValue: String?): String {
                return "$parentValue + child"
            }
        }

        assertEquals("init", ttl.get())
        ttl.set(null)
        assertNull(ttl.get())

        val task = FutureTask {
            ttl.get()
        }
        thread { task.run() }.join()

        assertNull(ttl.get())
        assertEquals("null + child", task.get())

        //////////////////////////////////////

        val task2 = FutureTask {
            ttl.get()
        }
        thread { task2.run() }.join()

        assertNull(ttl.get())
        assertEquals("null + child", task.get())
    }
}
