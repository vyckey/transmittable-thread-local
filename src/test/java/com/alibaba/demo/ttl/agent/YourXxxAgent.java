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
package com.alibaba.demo.ttl.agent;

import com.alibaba.ttl.threadpool.agent.TtlAgent;
import java.lang.instrument.Instrumentation;
import java.util.logging.Logger;

/** @author Jerry Lee (oldratlee at gmail dot com) */
public final class YourXxxAgent {
    private static final Logger logger = Logger.getLogger(YourXxxAgent.class.getName());

    public static void premain(String agentArgs, Instrumentation inst) throws Exception {
        TtlAgent.premain(agentArgs, inst); // add TTL Transformer

        // add your Transformer
        // ...
    }
}
