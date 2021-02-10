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
package com.alibaba.ttl.threadpool.agent;

import com.alibaba.ttl.threadpool.agent.logging.Logger;
import com.alibaba.ttl.threadpool.agent.transformlet.ClassInfo;
import com.alibaba.ttl.threadpool.agent.transformlet.TtlTransformlet;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;

/**
 * TTL {@link ClassFileTransformer} of Java Agent
 *
 * @author Jerry Lee (oldratlee at gmail dot com)
 * @see ClassFileTransformer
 * @see <a
 *     href="https://docs.oracle.com/javase/10/docs/api/java/lang/instrument/package-summary.html">The
 *     mechanism for instrumentation</a>
 * @since 0.9.0
 */
public class TtlTransformer implements ClassFileTransformer {
    private static final Logger logger = Logger.getLogger(TtlTransformer.class);

    /**
     * "<code>null</code> if no transform is performed", see {@code @return} of {@link
     * ClassFileTransformer#transform(ClassLoader, String, Class, ProtectionDomain, byte[])}
     */
    @SuppressFBWarnings({"EI_EXPOSE_REP"})
    // [ERROR] com.alibaba.ttl.threadpool.agent.TtlTransformer.transform(ClassLoader, String, Class,
    // ProtectionDomain, byte[])
    //         may expose internal representation by returning TtlTransformer.NO_TRANSFORM
    // the value is null, so there is NO "EI_EXPOSE_REP" problem actually.
    private static final byte[] NO_TRANSFORM = null;

    private final TtlExtensionTransformletManager extensionTransformletManager;
    private final List<TtlTransformlet> transformletList = new ArrayList<TtlTransformlet>();
    private final boolean logClassTransform;

    TtlTransformer(List<? extends TtlTransformlet> transformletList, boolean logClassTransform) {
        extensionTransformletManager = new TtlExtensionTransformletManager();

        this.logClassTransform = logClassTransform;
        for (TtlTransformlet ttlTransformlet : transformletList) {
            this.transformletList.add(ttlTransformlet);
            logger.info(
                    "[TtlTransformer] add Transformlet " + ttlTransformlet.getClass().getName());
        }
    }

    /**
     * info about class loader: may be <code>null</code> if the bootstrap loader.
     *
     * <p>more info see {@link ClassFileTransformer#transform(java.lang.ClassLoader,
     * java.lang.String, java.lang.Class, java.security.ProtectionDomain, byte[])}
     */
    @Override
    public final byte[] transform(
            @Nullable final ClassLoader loader,
            @Nullable final String classFile,
            final Class<?> classBeingRedefined,
            final ProtectionDomain protectionDomain,
            @NonNull final byte[] classFileBuffer) {
        try {
            // Lambda has no class file, no need to transform, just return.
            if (classFile == null) return NO_TRANSFORM;

            final ClassInfo classInfo = new ClassInfo(classFile, classFileBuffer, loader);
            if (logClassTransform)
                logger.info(
                        "[TtlTransformer] transforming "
                                + classInfo.getClassName()
                                + " from classloader "
                                + classInfo.getClassLoader()
                                + " at location "
                                + classInfo.getLocationUrl());

            extensionTransformletManager.collectExtensionTransformlet(classInfo);

            for (TtlTransformlet transformlet : transformletList) {
                transformlet.doTransform(classInfo);
                if (classInfo.isModified()) {
                    logger.info(
                            "[TtlTransformer] "
                                    + transformlet.getClass().getName()
                                    + " transformed "
                                    + classInfo.getClassName()
                                    + " from classloader "
                                    + classInfo.getClassLoader()
                                    + " at location "
                                    + classInfo.getLocationUrl());
                    return classInfo.getCtClass().toBytecode();
                }
            }

            final String transformlet =
                    extensionTransformletManager.extensionTransformletDoTransform(classInfo);
            if (classInfo.isModified()) {
                logger.info(
                        "[TtlTransformer] "
                                + transformlet
                                + " transformed "
                                + classInfo.getClassName()
                                + " from classloader "
                                + classInfo.getClassLoader()
                                + " at location "
                                + classInfo.getLocationUrl());
                return classInfo.getCtClass().toBytecode();
            }
        } catch (Throwable t) {
            String msg =
                    "[TtlTransformer] fail to transform class "
                            + classFile
                            + ", cause: "
                            + t.toString();
            logger.error(msg, t);
            throw new IllegalStateException(msg, t);
        }

        return NO_TRANSFORM;
    }
}
