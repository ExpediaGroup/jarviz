/*
* Copyright 2020 Expedia, Inc.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.vrbo.jarviz.visitor;

import org.objectweb.asm.Handle;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.vrbo.jarviz.model.Collector;
import com.vrbo.jarviz.model.Method;
import com.vrbo.jarviz.model.MethodCoupling;

import static com.vrbo.jarviz.util.NamingUtils.toSourceCodeFormat;

public class FilteredMethodVisitor extends MethodVisitor {

    private final Method sourceMethod;

    private final Collector collect;

    public FilteredMethodVisitor(final Method sourceMethod,
                                 final MethodVisitor methodVisitor,
                                 final Collector collect) {
        super(Opcodes.ASM7, methodVisitor);
        this.sourceMethod = sourceMethod;
        this.collect = collect;
    }

    @Override
    public void visitMethodInsn(final int opcode,
                                final String owner,
                                final String name,
                                final String descriptor,
                                final boolean isInterface) {
        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
        handleTargetMethod(owner, name, descriptor);
    }

    @Override
    public void visitInvokeDynamicInsn(final String name,
                                       final String descriptor,
                                       final Handle handle,
                                       final Object... bootstrapMethodArguments) {
        super.visitInvokeDynamicInsn(name, descriptor, handle, bootstrapMethodArguments);
        handleTargetMethod(handle.getOwner(), handle.getName(), handle.getDesc());
    }

    private void handleTargetMethod(final String targetClassName,
                                    final String targetMethodName,
                                    final String targetMethodDescriptor) {
        final Method targetMethod = new Method.Builder()
                                        .className(cleanseClassName(toSourceCodeFormat(targetClassName)))
                                        .methodName(targetMethodName)
                                        .build();

        collect.collectMethodCoupling(
            new MethodCoupling.Builder()
                .source(sourceMethod)
                .target(targetMethod)
                .build()
        );
    }

    static String cleanseClassName(final String methodName) {
        if (methodName.startsWith("WEB-INF.classes.")) {
            return methodName.substring(16);
        }

        return methodName;
    }
}
