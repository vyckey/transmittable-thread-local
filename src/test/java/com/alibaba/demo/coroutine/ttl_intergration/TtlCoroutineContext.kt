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
package com.alibaba.demo.coroutine.ttl_intergration

import com.alibaba.ttl.TransmittableThreadLocal.Transmitter.*
import kotlinx.coroutines.ThreadContextElement
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * @see [kotlinx.coroutines.asContextElement]
 */
fun ttlContext(): CoroutineContext =
//        if (TtlAgent.isTtlAgentLoaded()) // FIXME Open the if when implement TtlAgent for koroutine
//            EmptyCoroutineContext
//        else
            TtlElement()

/**
 * @see [kotlinx.coroutines.internal.ThreadLocalElement]
 */
internal class TtlElement : ThreadContextElement<Any> {
    companion object Key : CoroutineContext.Key<TtlElement>

    override val key: CoroutineContext.Key<*> get() = Key

    private var captured: Any =
            capture()

    override fun updateThreadContext(context: CoroutineContext): Any =
            replay(captured)

    override fun restoreThreadContext(context: CoroutineContext, oldState: Any) {
        captured = capture() // FIXME This capture operation is a MUST, WHY? This operation is too expensive?!
        restore(oldState)
    }

    // this method is overridden to perform value comparison (==) on key
    override fun minusKey(key: CoroutineContext.Key<*>): CoroutineContext =
            if (Key == key) EmptyCoroutineContext else this

    // this method is overridden to perform value comparison (==) on key
    override operator fun <E : CoroutineContext.Element> get(key: CoroutineContext.Key<E>): E? =
            @Suppress("UNCHECKED_CAST")
            if (Key == key) this as E else null
}
