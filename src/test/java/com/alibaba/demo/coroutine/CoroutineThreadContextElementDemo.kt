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
package  com.alibaba.demo.coroutine

import kotlinx.coroutines.*

private val threadLocal = ThreadLocal<String?>() // declare thread-local variable

/**
 * [Thread-local data - Coroutine Context and Dispatchers - Kotlin Programming Language](https://kotlinlang.org/docs/reference/coroutines/coroutine-context-and-dispatchers.html#thread-local-data)
 */
fun main() = runBlocking {
    threadLocal.set("main")
    println("Pre-main, current thread: ${Thread.currentThread()}, thread local value: ${threadLocal.get()}")

    val block: suspend CoroutineScope.() -> Unit = {
        println("Launch start, current thread: ${Thread.currentThread()}, thread local value: ${threadLocal.get()}")
        yield()
        println("After yield, current thread: ${Thread.currentThread()}, thread local value: ${threadLocal.get()}")
    }

    println()
    launch(block = block).join()

    println()
    launch(threadLocal.asContextElement(value = "launch"), block = block).join()

    println()
    launch(Dispatchers.Default, block = block).join()

    println()
    launch(Dispatchers.Default + threadLocal.asContextElement(value = "launch"), block = block).join()

    println()
    println("Post-main, current thread: ${Thread.currentThread()}, thread local value: ${threadLocal.get()}")
}
