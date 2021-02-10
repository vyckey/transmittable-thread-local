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

import com.alibaba.support.junit.conditional.BelowJava7
import com.alibaba.support.junit.conditional.ConditionalIgnoreRule
import com.alibaba.support.junit.conditional.ConditionalIgnoreRule.ConditionalIgnore
import com.alibaba.ttl.TtlUnwrap
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.ForkJoinPool

class TtlForkJoinPoolHelperTest {
    @Rule
    @JvmField
    val rule = ConditionalIgnoreRule()

    @Test
    @ConditionalIgnore(condition = BelowJava7::class)
    fun test_DisableInheritableForkJoinWorkerThreadFactory() {
        TtlForkJoinPoolHelper.getDefaultDisableInheritableForkJoinWorkerThreadFactory().let {
            assertTrue(it is DisableInheritableForkJoinWorkerThreadFactory)
            assertTrue(TtlForkJoinPoolHelper.isDisableInheritableForkJoinWorkerThreadFactory(it))

            assertSame(ForkJoinPool.defaultForkJoinWorkerThreadFactory, TtlForkJoinPoolHelper.unwrap(it))
            assertSame(ForkJoinPool.defaultForkJoinWorkerThreadFactory, TtlUnwrap.unwrap(it))
        }
    }

    @Test
    @ConditionalIgnore(condition = BelowJava7::class)
    fun test_null() {
        assertFalse(TtlForkJoinPoolHelper.isDisableInheritableForkJoinWorkerThreadFactory(null))
        assertNull(TtlForkJoinPoolHelper.unwrap(null))
    }
}
