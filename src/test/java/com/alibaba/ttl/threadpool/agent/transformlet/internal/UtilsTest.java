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

import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

public class UtilsTest {
    @Test
    public void test_get_unboxing_boolean_fromMap() {
        Map<String, Object> map = new HashMap<String, Object>();

        try {
            getUnboxingBoolean(map, "not_existed");
            fail();
        } catch (NullPointerException expected) {
            // do nothing
        }
    }

    private static boolean getUnboxingBoolean(Map<String, Object> map, String key) {
        return (Boolean) map.get(key);
    }
}
