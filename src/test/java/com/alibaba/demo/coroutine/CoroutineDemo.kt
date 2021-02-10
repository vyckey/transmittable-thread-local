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

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

/**
 * Kotlin coroutine related material:
 *
 * - Official docs:
 *      - [Coroutines Guide - Kotlin Programming Language](https://kotlinlang.org/docs/reference/coroutines/coroutines-guide.html)
 *      - [Coroutine Context and Dispatchers - Kotlin Programming Language](https://kotlinlang.org/docs/reference/coroutines/coroutine-context-and-dispatchers.html)
 *      - [Structured concurrency – Roman Elizarov](https://medium.com/@elizarov/structured-concurrency-722d765aa952)
 *      - [Kotlin/kotlin-coroutines-examples: Design documents and examples for coroutines in Kotlin - github.com](https://github.com/Kotlin/kotlin-coroutines-examples)
 * - others
 *      - [Demystifying Kotlin Coroutines – ProAndroidDev](https://proandroiddev.com/demystifying-kotlin-coroutines-6fe1f410570b)
 *      - [Kotlin coroutines in Spring - Code for glory](http://blog.alexnesterov.com/post/kotlin-coroutines-in-spring/)
 *      - [Kotlin coroutines and Spring 5 - Code for glory](http://blog.alexnesterov.com/post/kotlin-coroutines-and-spring-5/)
 */
fun main(): Unit = runBlocking {
    println("[${Thread.currentThread().name}] main")

    val deferred = async(Dispatchers.IO) {
        println("[${Thread.currentThread().name}] async")
        "world"
    }

    println("[${Thread.currentThread().name}] Hello ${deferred.await()}!")
}


