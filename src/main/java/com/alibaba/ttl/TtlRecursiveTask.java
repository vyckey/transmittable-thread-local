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

import static com.alibaba.ttl.TransmittableThreadLocal.Transmitter.*;

import com.alibaba.ttl.spi.TtlEnhanced;
import java.util.concurrent.ForkJoinTask;

/**
 * A recursive result-bearing {@link ForkJoinTask} enhanced by {@link TransmittableThreadLocal}.
 *
 * <p>Recommend to use {@link com.alibaba.ttl.threadpool.agent.TtlAgent}; Specially for {@code Java
 * 8} {@link java.util.stream.Stream} and {@link java.util.concurrent.CompletableFuture}, these
 * async task are executed by {@link java.util.concurrent.ForkJoinPool} via {@link ForkJoinTask} at
 * the bottom.
 *
 * @author LNAmp
 * @see java.util.concurrent.RecursiveTask
 * @see com.alibaba.ttl.threadpool.TtlForkJoinPoolHelper
 * @see com.alibaba.ttl.threadpool.agent.TtlAgent
 * @since 2.4.0
 */
public abstract class TtlRecursiveTask<V> extends ForkJoinTask<V> implements TtlEnhanced {

    private static final long serialVersionUID = 1814679366926362436L;

    private final Object captured = capture();

    protected TtlRecursiveTask() {}

    /** The result of the computation. */
    V result;

    /**
     * The main computation performed by this task.
     *
     * @return the result of the computation
     */
    protected abstract V compute();

    /** see {@link ForkJoinTask#getRawResult()} */
    public final V getRawResult() {
        return result;
    }

    /** see {@link ForkJoinTask#setRawResult(Object)} */
    protected final void setRawResult(V value) {
        result = value;
    }

    /** Implements execution conventions for RecursiveTask. */
    protected final boolean exec() {
        final Object backup = replay(captured);
        try {
            result = compute();
            return true;
        } finally {
            restore(backup);
        }
    }
}
