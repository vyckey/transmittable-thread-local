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
import edu.umd.cs.findbugs.annotations.Nullable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * {@link TtlAttachments} delegate/implementation.
 *
 * @author Jerry Lee (oldratlee at gmail dot com)
 * @see com.alibaba.ttl.TtlRunnable
 * @see com.alibaba.ttl.TtlCallable
 * @since 2.11.0
 */
public class TtlAttachmentsDelegate implements TtlAttachments {
    private final ConcurrentMap<String, Object> attachments =
            new ConcurrentHashMap<String, Object>();

    @Override
    public void setTtlAttachment(@NonNull String key, Object value) {
        attachments.put(key, value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getTtlAttachment(@NonNull String key) {
        return (T) attachments.get(key);
    }

    // ======== AutoWrapper Util Methods ========

    /**
     * @see TtlAttachments#KEY_IS_AUTO_WRAPPER
     * @since 2.13.0
     */
    public static boolean isAutoWrapper(@Nullable Object ttlAttachments) {
        if (notTtlAttachments(ttlAttachments)) return false;

        final Boolean value =
                ((TtlAttachments) ttlAttachments).getTtlAttachment(KEY_IS_AUTO_WRAPPER);
        if (value == null) return false;

        return value;
    }

    /**
     * @see TtlAttachments#KEY_IS_AUTO_WRAPPER
     * @since 2.13.0
     */
    public static void setAutoWrapperAttachment(@Nullable Object ttlAttachment) {
        if (TtlAttachmentsDelegate.notTtlAttachments(ttlAttachment)) return;
        ((TtlAttachments) ttlAttachment).setTtlAttachment(TtlAttachments.KEY_IS_AUTO_WRAPPER, true);
    }

    /**
     * @see TtlAttachments#KEY_IS_AUTO_WRAPPER
     * @since 2.13.0
     */
    @Nullable
    public static <T> T unwrapIfIsAutoWrapper(@Nullable T obj) {
        if (isAutoWrapper(obj)) return TtlUnwrap.unwrap(obj);
        else return obj;
    }

    /** @see TtlAttachments#KEY_IS_AUTO_WRAPPER */
    private static boolean notTtlAttachments(@Nullable Object ttlAttachment) {
        return !(ttlAttachment instanceof TtlAttachments);
    }
}
