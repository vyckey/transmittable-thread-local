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

import com.alibaba.ttl.spi.TtlWrapper;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Util methods for TTL Wrapper: unwrap TTL Wrapper and check TTL Wrapper.
 *
 * <p><b><i>Note:</i></b><br>
 * all methods is {@code null}-safe, when input parameter is {@code null}, return {@code null}.
 *
 * <p><b><i>Implementation Note:</i></b><br>
 * The util methods in this class should have been inside {@link TtlWrappers}.<br>
 * But for {@code Java 6} support, it's required splitting the util methods which involved {@code
 * Java 8} from {@link TtlWrappers}. In order to avoid loading {@code Java 8} class (eg: {@link
 * java.util.function.Consumer}, {@link java.util.function.Supplier}), when invoking any methods of
 * {@link TtlWrappers}.
 *
 * @author Jerry Lee (oldratlee at gmail dot com)
 * @see TtlRunnable
 * @see TtlCallable
 * @see com.alibaba.ttl.threadpool.TtlExecutors
 * @see com.alibaba.ttl.threadpool.TtlForkJoinPoolHelper
 * @see TtlWrappers
 * @since 2.11.4
 */
public class TtlUnwrap {
    /**
     * Generic unwrap method, unwrap {@link TtlWrapper} to the original/underneath one.
     *
     * <p>this method is {@code null}-safe, when input parameter is {@code null}, return {@code
     * null}; if input parameter is not a {@link TtlWrapper} just return input.
     *
     * @see TtlRunnable#unwrap(Runnable)
     * @see TtlCallable#unwrap(java.util.concurrent.Callable)
     * @see com.alibaba.ttl.threadpool.TtlExecutors#unwrap(java.util.concurrent.Executor)
     * @see com.alibaba.ttl.threadpool.TtlExecutors#unwrap(java.util.concurrent.ThreadFactory)
     * @see
     *     com.alibaba.ttl.threadpool.TtlForkJoinPoolHelper#unwrap(java.util.concurrent.ForkJoinPool.ForkJoinWorkerThreadFactory)
     * @see TtlWrappers#wrap(java.util.function.Supplier)
     * @see TtlWrappers#wrap(java.util.function.Consumer)
     * @see TtlWrappers#wrap(java.util.function.BiConsumer)
     * @see TtlWrappers#wrap(java.util.function.Function)
     * @see TtlWrappers#wrap(java.util.function.BiFunction)
     * @see #isWrapper(Object)
     * @since 2.11.4
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public static <T> T unwrap(@Nullable T obj) {
        if (!isWrapper(obj)) return obj;
        else return ((TtlWrapper<T>) obj).unwrap();
    }

    /**
     * check the input object is a {@code TtlWrapper} or not.
     *
     * @see #unwrap(Object)
     * @since 2.11.4
     */
    public static <T> boolean isWrapper(@Nullable T obj) {
        return obj instanceof TtlWrapper;
    }

    private TtlUnwrap() {
        throw new InstantiationError("Must not instantiate this class");
    }
}
