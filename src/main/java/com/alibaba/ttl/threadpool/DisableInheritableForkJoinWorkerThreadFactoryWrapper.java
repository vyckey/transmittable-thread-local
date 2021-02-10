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
package com.alibaba.ttl.threadpool;

import static com.alibaba.ttl.TransmittableThreadLocal.Transmitter.clear;
import static com.alibaba.ttl.TransmittableThreadLocal.Transmitter.restore;

import edu.umd.cs.findbugs.annotations.NonNull;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinPool.ForkJoinWorkerThreadFactory;
import java.util.concurrent.ForkJoinWorkerThread;

/**
 * @author Jerry Lee (oldratlee at gmail dot com)
 * @since 2.10.1
 */
class DisableInheritableForkJoinWorkerThreadFactoryWrapper
        implements DisableInheritableForkJoinWorkerThreadFactory {
    private final ForkJoinWorkerThreadFactory threadFactory;

    DisableInheritableForkJoinWorkerThreadFactoryWrapper(
            @NonNull ForkJoinWorkerThreadFactory threadFactory) {
        this.threadFactory = threadFactory;
    }

    @Override
    public ForkJoinWorkerThread newThread(ForkJoinPool pool) {
        final Object backup = clear();
        try {
            return threadFactory.newThread(pool);
        } finally {
            restore(backup);
        }
    }

    @Override
    @NonNull
    public ForkJoinWorkerThreadFactory unwrap() {
        return threadFactory;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DisableInheritableForkJoinWorkerThreadFactoryWrapper that =
                (DisableInheritableForkJoinWorkerThreadFactoryWrapper) o;

        return threadFactory.equals(that.threadFactory);
    }

    @Override
    public int hashCode() {
        return threadFactory.hashCode();
    }

    @Override
    public String toString() {
        return this.getClass().getName() + " - " + threadFactory.toString();
    }
}
