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
package com.alibaba.ttl.testmodel

import com.alibaba.CHILD_CREATE
import com.alibaba.PARENT_CREATE_MODIFIED_IN_CHILD
import com.alibaba.copyTtlValues
import com.alibaba.ttl.TransmittableThreadLocal
import java.util.concurrent.ConcurrentMap

/**
 * @author Jerry Lee (oldratlee at gmail dot com)
 */
class FooTask(private val value: String, private val ttlInstances: ConcurrentMap<String, TransmittableThreadLocal<FooPojo>>) : Runnable {

    @Volatile
    lateinit var copied: Map<String, FooPojo>

    override fun run() {
        try {
            // Add new
            val child = DeepCopyFooTransmittableThreadLocal()
            child.set(FooPojo(CHILD_CREATE + value, 3))
            ttlInstances[CHILD_CREATE + value] = child

            // modify the parent key
            ttlInstances[PARENT_CREATE_MODIFIED_IN_CHILD]!!.get()!!.name = ttlInstances[PARENT_CREATE_MODIFIED_IN_CHILD]!!.get()!!.name + value

            copied = copyTtlValues(ttlInstances)

            println("Task $value finished!")
        } catch (e: Throwable) {
            e.printStackTrace()
        }

    }
}
