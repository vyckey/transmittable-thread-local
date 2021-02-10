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
package com.alibaba.demo.ttl

import com.alibaba.ttl.TransmittableThreadLocal
import com.alibaba.ttl.TtlCallable
import com.alibaba.ttl.TtlRunnable
import java.util.concurrent.Callable
import java.util.concurrent.Executors

/**
 * @author Jerry Lee (oldratlee at gmail dot com)
 */
fun main() {
    val executorService = Executors.newCachedThreadPool()
    val context = TransmittableThreadLocal<String>()

    context.set("value-set-in-parent")
    println("[parent thread] set ${context.get()}")

    /////////////////////////////////////
    // Runnable / TtlRunnable
    /////////////////////////////////////
    val task = Runnable { println("[child thread] get ${context.get()} in Runnable") }
    val ttlRunnable = TtlRunnable.get(task)!!

    executorService.submit(ttlRunnable).get()

    /////////////////////////////////////
    // Callable / TtlCallable
    /////////////////////////////////////
    val call = Callable {
        println("[child thread] get ${context.get()} in Callable")
        42
    }
    val ttlCallable = TtlCallable.get(call)!!

    executorService.submit(ttlCallable).get()

    /////////////////////////////////////
    // cleanup
    /////////////////////////////////////
    executorService.shutdown()
}
