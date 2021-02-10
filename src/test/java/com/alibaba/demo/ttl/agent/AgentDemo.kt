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
@file:JvmName("AgentDemo")

package com.alibaba.demo.ttl.agent

import com.alibaba.ttl.TransmittableThreadLocal
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * @author Jerry Lee (oldratlee at gmail dot com)
 */
fun main() {
    val executorService = Executors.newFixedThreadPool(3)
    expandThreadPool(executorService)

    stringTransmittableThreadLocal.set("foo - main")
    personReferenceTransmittableThreadLocal.set(Person("jerry - reference", 1))
    personCopyTransmittableThreadLocal.set(Person("Tom - value", 2))

    printTtlInstancesInfo("Main - Before execution of thread pool")

    val submit = executorService.submit {
        printTtlInstancesInfo("Thread Pool - enter")
        stringTransmittableThreadLocal.set("foo - modified in thread pool")
        personReferenceTransmittableThreadLocal.get().name = "jerry - reference - modified in thread pool"
        personCopyTransmittableThreadLocal.get().name = "Tom - value - modified in thread pool"
        printTtlInstancesInfo("Thread Pool - leave")
    }
    submit.get()

    printTtlInstancesInfo("Main - After execution of thread pool")

    executorService.shutdown()
}

private data class Person(var name: String = "unnamed", var age: Int = -1)

private val stringTransmittableThreadLocal = TransmittableThreadLocal<String>()

private val personReferenceTransmittableThreadLocal = object : TransmittableThreadLocal<Person>() {
    override fun initialValue(): Person = Person()
}

private val personCopyTransmittableThreadLocal = object : TransmittableThreadLocal<Person>() {
    override fun initialValue(): Person = Person()

    override fun copy(parentValue: Person): Person = parentValue.copy() // copy value to child thread
}

private fun expandThreadPool(executor: ExecutorService) {
    (0 until 3).map {
        executor.submit { Thread.sleep(100) }
    }.forEach {
        it.get()
    }
}

private fun printTtlInstancesInfo(msg: String) {
    println("====================================================")
    println(msg)
    println("====================================================")
    println("stringTransmittableThreadLocal: ${stringTransmittableThreadLocal.get()}")
    println("personReferenceTransmittableThreadLocal: ${personReferenceTransmittableThreadLocal.get()}")
    println("personCopyTransmittableThreadLocal: ${personCopyTransmittableThreadLocal.get()}")
}
