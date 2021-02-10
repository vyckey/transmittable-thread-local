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
package com.alibaba.ttl.forkjoin

import com.alibaba.*
import com.alibaba.support.junit.conditional.BelowJava7
import com.alibaba.support.junit.conditional.ConditionalIgnoreRule
import com.alibaba.support.junit.conditional.ConditionalIgnoreRule.ConditionalIgnore
import com.alibaba.ttl.TtlCallable
import com.alibaba.ttl.TtlRunnable
import com.alibaba.ttl.testmodel.Call
import com.alibaba.ttl.testmodel.Task
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.ForkJoinPool

private val pool = ForkJoinPool()

class ForkJoinPool4RunnableCallableTest {
    @Rule
    @JvmField
    val rule = ConditionalIgnoreRule()

    @Test
    @ConditionalIgnore(condition = BelowJava7::class)
    fun test_Runnable() {
        val ttlInstances = createParentTtlInstances()

        val task = Task("1", ttlInstances)
        val ttlRunnable = if (noTtlAgentRun()) TtlRunnable.get(task) else task

        if (noTtlAgentRun()) {
            // create after new Task, won't see parent value in in task!
            createParentTtlInstancesAfterCreateChild(ttlInstances)
        }
        val submit = pool.submit(ttlRunnable)
        if (!noTtlAgentRun()) {
            // create after new Task, won't see parent value in in task!
            createParentTtlInstancesAfterCreateChild(ttlInstances)
        }


        submit.get()


        // child Inheritable
        assertChildTtlValues("1", task.copied)

        // child do not effect parent
        assertParentTtlValues(copyTtlValues(ttlInstances))
    }

    @Test
    @ConditionalIgnore(condition = BelowJava7::class)
    fun test_Callable() {
        val ttlInstances = createParentTtlInstances()

        val call = Call("1", ttlInstances)
        val ttlCallable = if (noTtlAgentRun()) TtlCallable.get(call) else call

        if (noTtlAgentRun()) {
            // create after new Task, won't see parent value in in task!
            createParentTtlInstancesAfterCreateChild(ttlInstances)
        }
        val future = pool.submit(ttlCallable)
        if (!noTtlAgentRun()) {
            // create after new Task, won't see parent value in in task!
            createParentTtlInstancesAfterCreateChild(ttlInstances)
        }

        Assert.assertEquals("ok", future.get())


        // child Inheritable
        assertChildTtlValues("1", call.copied)

        // child do not effect parent
        assertParentTtlValues(copyTtlValues(ttlInstances))
    }
}
