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

import com.alibaba.support.junit.conditional.ConditionalIgnoreRule
import com.alibaba.support.junit.conditional.ConditionalIgnoreRule.ConditionalIgnore
import com.alibaba.support.junit.conditional.IsAgentRun
import com.alibaba.support.junit.conditional.NoAgentRun
import com.alibaba.ttl.TtlRunnable
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

class MyThreadPoolExecutor : ThreadPoolExecutor(10, 20, 2, TimeUnit.SECONDS, LinkedBlockingQueue()) {
    val runnableList = CopyOnWriteArrayList<Runnable>()

    override fun afterExecute(r: Runnable, t: Throwable?) {
        runnableList.add(r)
        super.afterExecute(r, t)
    }

    override fun beforeExecute(t: Thread, r: Runnable) {
        runnableList.add(r)
        super.beforeExecute(t, r)
    }
}

class MyRunnable : Runnable {
    override fun run() {
        Thread.sleep(1)
    }
}

class BeforeAndAfterExecuteMethodOfExecutorSubclassTest {
    @Test
    @ConditionalIgnore(condition = NoAgentRun::class)
    fun underAgent() {
        val myThreadPoolExecutor = MyThreadPoolExecutor()

        (0 until 10).map {
            myThreadPoolExecutor.execute(MyRunnable())
        }

        Thread.sleep(100)

        assertEquals(20, myThreadPoolExecutor.runnableList.size)
        assertTrue(myThreadPoolExecutor.runnableList.all { it is MyRunnable })
    }

    @Test
    @ConditionalIgnore(condition = IsAgentRun::class)
    fun noAgent() {
        val myThreadPoolExecutor = MyThreadPoolExecutor()

        val ttlExecutorService = myThreadPoolExecutor.let {
            it.setKeepAliveTime(10, TimeUnit.SECONDS)
            TtlExecutors.getTtlExecutorService(it)
        }!!

        (0 until 10).map {
            ttlExecutorService.execute(MyRunnable())
        }

        Thread.sleep(100)

        assertEquals(20, myThreadPoolExecutor.runnableList.size)
        assertTrue(myThreadPoolExecutor.runnableList.all { it is TtlRunnable })
    }

    @Rule
    @JvmField
    val rule = ConditionalIgnoreRule()
}
