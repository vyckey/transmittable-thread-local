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
package com.alibaba.ttl.threadpool.agent.transformlet.helper

import com.alibaba.support.junit.conditional.ConditionalIgnoreRule
import com.alibaba.support.junit.conditional.ConditionalIgnoreRule.ConditionalIgnore
import com.alibaba.support.junit.conditional.IsAgentRun
import com.alibaba.ttl.threadpool.agent.TtlAgentLoggerInitializer
import com.alibaba.ttl.threadpool.agent.transformlet.helper.TtlTransformletHelper.getLocationFileOfClass
import javassist.ClassPool
import org.apache.commons.lang3.StringUtils
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

class TtlTransformletHelperTest {
    @Rule
    @JvmField
    val rule = ConditionalIgnoreRule()

    @Test
    fun test_getFileLocationOfClass_javaClass() {
        TtlAgentLoggerInitializer

        Assert.assertNull(getLocationFileOfClass(String::class.java))

        MatcherAssert.assertThat(
            getLocationFileOfClass(StringUtils::class.java),
            CoreMatchers.endsWith("/commons-lang3-3.5.jar")
        )
    }

    @Test
    @ConditionalIgnore(condition = IsAgentRun::class) // skip unit test for Javassist on agent, because Javassist is repackaged
    fun test_getFileLocationOfClass_ctClass() {
        TtlAgentLoggerInitializer

        val classPool = ClassPool(true)

        // Java 8: file:/path/to/jdk_8/jre/lib/rt.jar!/java/lang/String.class
        // Java 11: /java.base/java/lang/String.class
        MatcherAssert.assertThat(
            getLocationFileOfClass(classPool.getCtClass("java.lang.String")),
            CoreMatchers.endsWith("/java/lang/String.class")
        )

        // Java 8: file:/path/to/commons-lang3-3.5.jar!/org/apache/commons/lang3/StringUtils.class
        MatcherAssert.assertThat(
            getLocationFileOfClass(classPool.getCtClass("org.apache.commons.lang3.StringUtils")),
            CoreMatchers.endsWith("/commons-lang3-3.5.jar!/org/apache/commons/lang3/StringUtils.class")
        )
    }
}
