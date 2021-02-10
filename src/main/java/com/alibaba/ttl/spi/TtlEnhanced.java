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

/**
 * a Ttl marker/tag interface, for ttl enhanced class, for example {@code TTL wrapper} like {@link
 * com.alibaba.ttl.TtlRunnable}, {@link com.alibaba.ttl.TtlCallable}.
 *
 * @author Jerry Lee (oldratlee at gmail dot com)
 * @see com.alibaba.ttl.TtlRunnable
 * @see com.alibaba.ttl.TtlCallable
 * @see com.alibaba.ttl.TtlRecursiveAction
 * @see com.alibaba.ttl.TtlRecursiveTask
 * @see TtlAttachments
 * @since 2.11.0
 */
public interface TtlEnhanced {}
