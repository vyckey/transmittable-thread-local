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

import static com.alibaba.ttl.threadpool.agent.transformlet.helper.TtlTransformletHelper.addTryFinallyToMethod;
import static com.alibaba.ttl.threadpool.agent.transformlet.helper.TtlTransformletHelper.signatureOfMethod;

import com.alibaba.ttl.threadpool.agent.logging.Logger;
import com.alibaba.ttl.threadpool.agent.transformlet.ClassInfo;
import com.alibaba.ttl.threadpool.agent.transformlet.TtlTransformlet;
import edu.umd.cs.findbugs.annotations.NonNull;
import java.io.IOException;
import javassist.*;

/**
 * {@link TtlTransformlet} for {@link java.util.TimerTask}.
 *
 * @author Jerry Lee (oldratlee at gmail dot com)
 * @author wuwen5 (wuwen.55 at aliyun dot com)
 * @see java.util.TimerTask
 * @see java.util.Timer
 * @since 2.7.0
 */
public final class TimerTaskTtlTransformlet implements TtlTransformlet {
    private static final Logger logger = Logger.getLogger(TimerTaskTtlTransformlet.class);

    private static final String TIMER_TASK_CLASS_NAME = "java.util.TimerTask";
    private static final String RUN_METHOD_NAME = "run";

    @Override
    public void doTransform(@NonNull final ClassInfo classInfo)
            throws IOException, NotFoundException, CannotCompileException {
        if (TIMER_TASK_CLASS_NAME.equals(classInfo.getClassName()))
            return; // No need transform TimerTask class

        final CtClass clazz = classInfo.getCtClass();

        if (clazz.isPrimitive() || clazz.isArray() || clazz.isInterface() || clazz.isAnnotation()) {
            return;
        }
        // class contains method `void run()` ?
        try {
            final CtMethod runMethod = clazz.getDeclaredMethod(RUN_METHOD_NAME, new CtClass[0]);
            if (!CtClass.voidType.equals(runMethod.getReturnType())) return;
        } catch (NotFoundException e) {
            return;
        }
        if (!clazz.subclassOf(clazz.getClassPool().get(TIMER_TASK_CLASS_NAME))) return;

        logger.info("Transforming class " + classInfo.getClassName());

        updateTimerTaskClass(clazz);
        classInfo.setModified();
    }

    /**
     * @see
     *     com.alibaba.ttl.threadpool.agent.transformlet.helper.TtlTransformletHelper#doCaptureIfNotTtlEnhanced(Object)
     */
    private void updateTimerTaskClass(@NonNull final CtClass clazz)
            throws CannotCompileException, NotFoundException {
        final String className = clazz.getName();

        // add new field
        final String capturedFieldName = "captured$field$added$by$ttl";
        final CtField capturedField =
                CtField.make("private final Object " + capturedFieldName + ";", clazz);
        clazz.addField(
                capturedField,
                "com.alibaba.ttl.threadpool.agent.transformlet.helper.TtlTransformletHelper.doCaptureIfNotTtlEnhanced(this);");
        logger.info("add new field " + capturedFieldName + " to class " + className);

        final CtMethod runMethod = clazz.getDeclaredMethod(RUN_METHOD_NAME, new CtClass[0]);

        final String beforeCode =
                "Object backup = com.alibaba.ttl.TransmittableThreadLocal.Transmitter.replay("
                        + capturedFieldName
                        + ");";
        final String finallyCode =
                "com.alibaba.ttl.TransmittableThreadLocal.Transmitter.restore(backup);";

        final String code = addTryFinallyToMethod(runMethod, beforeCode, finallyCode);
        logger.info(
                "insert code around method "
                        + signatureOfMethod(runMethod)
                        + " of class "
                        + clazz.getName()
                        + ": "
                        + code);
    }
}
