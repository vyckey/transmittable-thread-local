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

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * The TTL attachments for TTL tasks, eg: {@link com.alibaba.ttl.TtlRunnable}, {@link
 * com.alibaba.ttl.TtlCallable}.
 *
 * @author Jerry Lee (oldratlee at gmail dot com)
 * @since 2.11.0
 */
public interface TtlAttachments extends TtlEnhanced {
    /**
     * set the TTL attachments for TTL tasks
     *
     * @param key attachment key
     * @param value attachment value
     * @since 2.11.0
     */
    void setTtlAttachment(@NonNull String key, Object value);

    /**
     * get the TTL attachment for TTL tasks
     *
     * @param key attachment key
     * @since 2.11.0
     */
    <T> T getTtlAttachment(@NonNull String key);

    /**
     * The attachment key of TTL task, weather this task is a auto wrapper task.
     *
     * <p>so the value of this attachment is a {@code boolean}.
     *
     * @since 2.11.0
     */
    String KEY_IS_AUTO_WRAPPER = "ttl.is.auto.wrapper";
}
