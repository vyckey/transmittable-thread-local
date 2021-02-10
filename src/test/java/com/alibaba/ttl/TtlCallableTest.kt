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

import com.alibaba.*
import com.alibaba.ttl.testmodel.Call
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.AfterClass
import org.junit.Assert.*
import org.junit.Test
import java.util.concurrent.Callable
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * @author Jerry Lee (oldratlee at gmail dot com)
 */
class TtlCallableTest {
    @Test
    fun test_TtlCallable_runInCurrentThread() {
        val ttlInstances = createParentTtlInstances()

        val call = Call("1", ttlInstances)


        val ttlCallable = TtlCallable.get(call)!!

        // create after new Task, won't see parent value in in task!
        createParentTtlInstancesAfterCreateChild(ttlInstances)


        // run in the *current* thread
        assertEquals("ok", ttlCallable.call())


        // child Inheritable
        assertChildTtlValues("1", call.copied)

        // child do not effect parent
        assertParentTtlValues(copyTtlValues(ttlInstances))
    }

    @Test
    fun test_TtlCallable_asyncRunByExecutorService() {
        val ttlInstances = createParentTtlInstances()

        val call = Call("1", ttlInstances)
        val ttlCallable = if (noTtlAgentRun()) TtlCallable.get(call) else call

        if (noTtlAgentRun()) {
            // create after new Task, won't see parent value in in task!
            createParentTtlInstancesAfterCreateChild(ttlInstances)
        }
        val future = executorService.submit(ttlCallable)
        if (!noTtlAgentRun()) {
            // create after new Task, won't see parent value in in task!
            createParentTtlInstancesAfterCreateChild(ttlInstances)
        }

        assertEquals("ok", future.get())


        // child Inheritable
        assertChildTtlValues("1", call.copied)

        // child do not effect parent
        assertParentTtlValues(copyTtlValues(ttlInstances))
    }

    @Test
    fun test_remove_sameAsNotSet() {
        val ttlInstances = createParentTtlInstances()


        // add and remove !!
        newTtlInstanceAndPut("add and removed!", ttlInstances).remove()

        val call = Call("1", ttlInstances)
        val ttlCallable = if (noTtlAgentRun()) TtlCallable.get(call) else call


        if (noTtlAgentRun()) {
            // create after new Task, won't see parent value in in task!
            createParentTtlInstancesAfterCreateChild(ttlInstances)
        }
        val future = executorService.submit(ttlCallable)
        if (!noTtlAgentRun()) {
            // create after new Task, won't see parent value in in task!
            createParentTtlInstancesAfterCreateChild(ttlInstances)
        }

        assertEquals("ok", future.get())


        // child Inheritable
        assertChildTtlValues("1", call.copied)

        // child do not effect parent
        assertParentTtlValues(copyTtlValues(ttlInstances))
    }

    @Test
    fun test_releaseTtlValueReferenceAfterCall() {
        val ttlInstances = createParentTtlInstances()

        val call = Call("1", ttlInstances)
        val ttlCallable = TtlCallable.get(call, true)!!
        assertSame(call, ttlCallable.callable)

        assertEquals("ok", executorService.submit(ttlCallable).get())

        try {
            executorService.submit(ttlCallable).get()
            fail()
        } catch (expected: ExecutionException) {
            assertThat<Throwable>(expected.cause, instanceOf(IllegalStateException::class.java))
            assertThat<String>(expected.message, containsString("TTL value reference is released after call!"))
        }

    }

    @Test
    fun test_get_same() {
        val call = Call("1")
        val ttlCallable = TtlCallable.get(call)!!
        assertSame(call, ttlCallable.callable)
    }

    @Test
    fun test_get_idempotent() {
        val call = TtlCallable.get(Call("1"))
        try {
            TtlCallable.get(call)
            fail()
        } catch (e: IllegalStateException) {
            assertThat<String>(e.message, containsString("Already TtlCallable"))
        }

    }

    @Test
    @Throws(Exception::class)
    fun test_get_nullInput() {
        assertNull(TtlCallable.get<Any>(null))
    }

    @Test
    fun test_gets() {
        val call1 = Call("1")
        val call2 = Call("2")
        val call3 = Call("3")

        val callList = TtlCallable.gets(
            listOf<Callable<String>?>(call1, call2, null, call3))

        assertEquals(4, callList.size.toLong())
        assertThat(callList[0], instanceOf(TtlCallable::class.java))
        assertThat(callList[1], instanceOf(TtlCallable::class.java))
        assertNull(callList[2])
        assertThat(callList[3], instanceOf(TtlCallable::class.java))
    }

    @Test
    fun test_unwrap() {
        assertNull(TtlCallable.unwrap<String>(null))

        val callable = Callable { "hello" }
        val ttlCallable = TtlCallable.get(callable)


        assertSame(callable, TtlCallable.unwrap(callable))
        assertSame(callable, TtlCallable.unwrap(ttlCallable))

        assertSame(callable, TtlUnwrap.unwrap(callable))
        assertSame(callable, TtlUnwrap.unwrap(ttlCallable))


        assertEquals(listOf(callable), TtlCallable.unwraps(listOf(callable)))
        assertEquals(listOf(callable), TtlCallable.unwraps(listOf(ttlCallable)))
        assertEquals(listOf(callable, callable), TtlCallable.unwraps(listOf(ttlCallable, callable)))
        assertEquals(listOf<Callable<String>>(), TtlCallable.unwraps<String>(null))
    }

    companion object {
        private val executorService = Executors.newFixedThreadPool(3).also { expandThreadPool(it) }

        @AfterClass
        @JvmStatic
        @Suppress("unused")
        fun afterClass() {
            executorService.shutdown()
            assertTrue("Fail to shutdown thread pool", executorService.awaitTermination(100, TimeUnit.MILLISECONDS))
        }
    }
}
