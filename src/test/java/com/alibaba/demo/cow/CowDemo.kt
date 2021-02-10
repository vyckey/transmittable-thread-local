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
package com.alibaba.demo.cow

import com.alibaba.expandThreadPool
import com.alibaba.ttl.TransmittableThreadLocal
import com.alibaba.ttl.threadpool.TtlExecutors
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

fun main() {
    val threadPool = Executors.newCachedThreadPool().let {
        expandThreadPool(it)
        TtlExecutors.getTtlExecutorService(it)
    }!!

    val traceContext = object : TransmittableThreadLocal<Trace>() {
        override fun initialValue(): Trace = Trace("init", Span("first", 0))
        override fun copy(parentValue: Trace): Trace = parentValue.copy() // shadow copy Trace, this is fast
        override fun childValue(parentValue: Trace): Trace = parentValue.copy() // shadow copy Trace, this is fast

        fun increaseSpan() {
            get().run {
                // COW the Span object in Trace
                span = span.copy(id = "${span.id} + PONG", counter = span.counter + 1)
            }
        }

        override fun toString(): String {
            return "${get()}[${super.toString()}]"
        }
    }

    fun printTtlInfo() {
        println("${Thread.currentThread().name}: $traceContext")
    }

    printTtlInfo()
    threadPool.execute {
        printTtlInfo()
        traceContext.increaseSpan()
        printTtlInfo()

        threadPool.execute {
            printTtlInfo()
            traceContext.increaseSpan()
            printTtlInfo()
        }
    }

    Thread.sleep(100)
    threadPool.shutdown()
    threadPool.awaitTermination(1, TimeUnit.SECONDS)
}

private data class Trace(var name: String, var span: Span)

private data class Span(val id: String, val counter: Int)
