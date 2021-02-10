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
package com.alibaba.ttl.threadpool.agent.transformlet;

import edu.umd.cs.findbugs.annotations.NonNull;
import java.io.IOException;
import javassist.CannotCompileException;
import javassist.NotFoundException;

/**
 * TTL {@code Transformlet}.
 *
 * @author Jerry Lee (oldratlee at gmail dot com)
 * @since 2.13.0
 */
public interface TtlTransformlet {
    /**
     * info about class loader: may be <code>null</code> if the bootstrap loader.
     *
     * <p>more info see {@link
     * java.lang.instrument.ClassFileTransformer#transform(java.lang.ClassLoader, java.lang.String,
     * java.lang.Class, java.security.ProtectionDomain, byte[])}
     *
     * @see com.alibaba.ttl.threadpool.agent.TtlTransformer#transform(ClassLoader, String, Class,
     *     java.security.ProtectionDomain, byte[])
     * @see java.lang.instrument.ClassFileTransformer#transform
     */
    void doTransform(@NonNull ClassInfo classInfo)
            throws CannotCompileException, NotFoundException, IOException;
}
