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
package com.alibaba.ttl.threadpool.agent

import com.alibaba.support.junit.conditional.ConditionalIgnoreRule
import com.alibaba.support.junit.conditional.IsAgentRun
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.util.*

class TtlExtensionTransformletManagerTest {
    @Rule
    @JvmField
    val rule = ConditionalIgnoreRule()

    @Test
    @ConditionalIgnoreRule.ConditionalIgnore(condition = IsAgentRun::class)
    fun test_readLines() {
        TtlAgentLoggerInitializer

        val classLoader = TtlExtensionTransformletManagerTest::class.java.classLoader
        val pair = TtlExtensionTransformletManager.readLinesFromExtensionFiles(
            classLoader.getResources("test_extension/foo.txt"), mutableMapOf()
        )
        val lines: LinkedHashSet<String> = pair.first

        assertEquals(
            linkedSetOf("hello.World", "hello.tabBefore", "hello.tabAfter", "hello.spaceBefore", "hello.spaceAfter"),
            lines
        )
    }
}
