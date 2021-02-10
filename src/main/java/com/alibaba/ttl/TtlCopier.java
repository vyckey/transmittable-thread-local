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
package com.alibaba.ttl;

/**
 * TtlCopier creates the value when {@link TransmittableThreadLocal.Transmitter#capture()}, use the
 * created value when {@link TransmittableThreadLocal.Transmitter#replay(Object)}
 *
 * @author Jerry Lee (oldratlee at gmail dot com)
 * @see TransmittableThreadLocal.Transmitter
 * @see TransmittableThreadLocal.Transmitter#capture()
 * @since 2.11.0
 */
@FunctionalInterface
public interface TtlCopier<T> {
    /**
     * Computes the value for {@link TransmittableThreadLocal} or registered {@link
     * ThreadLocal}(registered by method {@link
     * TransmittableThreadLocal.Transmitter#registerThreadLocal Transmitter#registerThreadLocal}) as
     * a function of the source thread's value at the time the task Object is created.
     *
     * <p>This method is called from {@link TtlRunnable} or {@link TtlCallable} when it create,
     * before the task is started (aka. called when {@link
     * TransmittableThreadLocal.Transmitter#capture()}).
     *
     * @see TransmittableThreadLocal.Transmitter#registerThreadLocal(ThreadLocal, TtlCopier)
     * @see TransmittableThreadLocal.Transmitter#registerThreadLocalWithShadowCopier(ThreadLocal)
     * @see TransmittableThreadLocal.Transmitter#unregisterThreadLocal
     */
    T copy(T parentValue);
}
