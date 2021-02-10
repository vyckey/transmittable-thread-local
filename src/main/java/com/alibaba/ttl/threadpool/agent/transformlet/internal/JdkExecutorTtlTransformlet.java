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
package com.alibaba.ttl.threadpool.agent.transformlet.internal;

import com.alibaba.ttl.threadpool.agent.TtlAgent;
import com.alibaba.ttl.threadpool.agent.transformlet.TtlTransformlet;
import com.alibaba.ttl.threadpool.agent.transformlet.helper.AbstractExecutorTtlTransformlet;
import java.util.HashSet;
import java.util.Set;

/**
 * {@link TtlTransformlet} for {@link java.util.concurrent.ThreadPoolExecutor} and {@link
 * java.util.concurrent.ScheduledThreadPoolExecutor}.
 *
 * @author Jerry Lee (oldratlee at gmail dot com)
 * @author wuwen5 (wuwen.55 at aliyun dot com)
 * @see java.util.concurrent.ThreadPoolExecutor
 * @see java.util.concurrent.ScheduledThreadPoolExecutor
 * @since 2.5.1
 */
public final class JdkExecutorTtlTransformlet extends AbstractExecutorTtlTransformlet
        implements TtlTransformlet {

    private static Set<String> getExecutorClassNames() {
        Set<String> executorClassNames = new HashSet<String>();

        executorClassNames.add(THREAD_POOL_EXECUTOR_CLASS_NAME);
        executorClassNames.add("java.util.concurrent.ScheduledThreadPoolExecutor");

        return executorClassNames;
    }

    public JdkExecutorTtlTransformlet() {
        super(getExecutorClassNames(), TtlAgent.isDisableInheritableForThreadPool());
    }
}
