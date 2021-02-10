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
package com.alibaba.ttl.spi;

import com.alibaba.ttl.TtlUnwrap;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Ttl Wrapper interface.
 *
 * <p>Used to mark wrapper types, for example:
 *
 * <ul>
 *   <li>{@link com.alibaba.ttl.TtlCallable}
 *   <li>{@link com.alibaba.ttl.threadpool.TtlExecutors}
 *   <li>{@link com.alibaba.ttl.threadpool.DisableInheritableThreadFactory}
 * </ul>
 *
 * @author Jerry Lee (oldratlee at gmail dot com)
 * @see TtlUnwrap#unwrap
 * @see com.alibaba.ttl.TtlCallable
 * @see com.alibaba.ttl.TtlRunnable
 * @see com.alibaba.ttl.threadpool.TtlExecutors
 * @see com.alibaba.ttl.threadpool.DisableInheritableThreadFactory
 * @see com.alibaba.ttl.threadpool.DisableInheritableForkJoinWorkerThreadFactory
 * @since 2.11.4
 */
public interface TtlWrapper<T> extends TtlEnhanced {
    /**
     * unwrap {@link TtlWrapper} to the original/underneath one.
     *
     * @see TtlUnwrap#unwrap(Object)
     */
    @NonNull
    T unwrap();
}
