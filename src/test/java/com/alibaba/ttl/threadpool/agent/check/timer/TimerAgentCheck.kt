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
@file:JvmName("TimerAgentCheck")

package com.alibaba.ttl.threadpool.agent.check.timer

import com.alibaba.*
import com.alibaba.ttl.testmodel.Task
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * @author Jerry Lee (oldratlee at gmail dot com)
 * @see com.alibaba.ttl.threadpool.agent.transformlet.internal.TimerTaskTtlTransformlet
 */
fun main() {
    val timer = Timer(true)

    printHead("TimerAgentCheck")

    val ttlInstances = createParentTtlInstances(ConcurrentHashMap())

    val tag = "1"
    val task = Task(tag, ttlInstances)

    val latch = CountDownLatch(1)
    val timerTask = object : TimerTask() {
        override fun run() {
            task.run()
            latch.countDown()
        }
    }
    timer.schedule(timerTask, 0)

    // create after new Task, won't see parent value in in task!
    createParentTtlInstancesAfterCreateChild(ttlInstances)

    latch.await(100, TimeUnit.MILLISECONDS)

    // child Inheritable
    assertChildTtlValues(tag, task.copied)
    // child do not effect parent
    assertParentTtlValues(copyTtlValues(ttlInstances))

    printHead("TimerAgentCheck OK!")
}

