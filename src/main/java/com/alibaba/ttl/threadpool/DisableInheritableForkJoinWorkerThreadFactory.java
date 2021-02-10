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

import com.alibaba.ttl.spi.TtlWrapper;
import edu.umd.cs.findbugs.annotations.NonNull;
import java.util.concurrent.ForkJoinPool.ForkJoinWorkerThreadFactory;

/**
 * Disable inheritable {@link ForkJoinWorkerThreadFactory}.
 *
 * @author Jerry Lee (oldratlee at gmail dot com)
 * @since 2.10.1
 */
public interface DisableInheritableForkJoinWorkerThreadFactory
        extends ForkJoinWorkerThreadFactory, TtlWrapper<ForkJoinWorkerThreadFactory> {
    /** Unwrap {@link DisableInheritableThreadFactory} to the original/underneath one. */
    @Override
    @NonNull
    ForkJoinWorkerThreadFactory unwrap();
}
