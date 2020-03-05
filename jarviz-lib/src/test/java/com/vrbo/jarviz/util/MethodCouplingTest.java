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

package com.vrbo.jarviz.util;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import com.vrbo.jarviz.config.CouplingFilterConfig;
import com.vrbo.jarviz.model.CouplingFilter;
import com.vrbo.jarviz.model.Method;
import com.vrbo.jarviz.model.MethodCoupling;
import com.vrbo.jarviz.service.UsageCollector;
import com.vrbo.jarviz.util.couplingtest.Bar;
import com.vrbo.jarviz.util.couplingtest.Foo;
import com.vrbo.jarviz.util.couplingtest.MySource;
import com.vrbo.jarviz.visitor.FilteredClassVisitor;

import static org.assertj.core.api.Assertions.assertThat;

public class MethodCouplingTest {

    @Test
    public void testCouplings() throws IOException {
        final CouplingFilterConfig filterConfig = new CouplingFilterConfig.Builder()
                                                      .include(
                                                          new CouplingFilter.Builder()
                                                              .targetPackage("com\\.vrbo\\.jarviz\\.util\\.couplingtest")
                                                              .build())
                                                      .build();
        final UsageCollector collector = new UsageCollector(filterConfig);

        final FilteredClassVisitor classVisitor = new FilteredClassVisitor(MySource.class.getName(), collector);

        classVisitor.visit();

        final List<MethodCoupling> couplings = collector.getMethodCouplings();

        assertThat(couplings).hasSize(17);

        // verify direct method calls
        assertThat(couplings).contains(
            new MethodCoupling.Builder()
                .source(new Method.Builder().className(MySource.class.getName()).methodName("doA1").build())
                .target(new Method.Builder().className(Foo.class.getName()).methodName("getFooIntVal").build())
                .build(),
            new MethodCoupling.Builder()
                .source(new Method.Builder().className(MySource.class.getName()).methodName("doA2").build())
                .target(new Method.Builder().className(Foo.class.getName()).methodName("getFooIntVal").build())
                .build(),
            new MethodCoupling.Builder()
                .source(new Method.Builder().className(MySource.class.getName()).methodName("doA3").build())
                .target(new Method.Builder().className(Foo.class.getName()).methodName("getFooIntVal").build())
                .build()
        );
        assertThat(couplings)
            .contains(
                new MethodCoupling.Builder()
                    .source(new Method.Builder().className(MySource.class.getName()).methodName("doB1").build())
                    .target(new Method.Builder().className(Bar.class.getName()).methodName("getVal").build())
                    .build());

        // verify chained method calls
        assertThat(couplings)
            .contains(
                new MethodCoupling.Builder()
                    .source(new Method.Builder().className(MySource.class.getName()).methodName("doB2").build())
                    .target(new Method.Builder().className(Foo.class.getName()).methodName("getFooBarVal").build())
                    .build())
            .contains(
                new MethodCoupling.Builder()
                    .source(new Method.Builder().className(MySource.class.getName()).methodName("doB2").build())
                    .target(new Method.Builder().className(Bar.class.getName()).methodName("getVal").build())
                    .build());

        // verify dynamic method calls (via lambdas)
        final List<MethodCoupling> getFooLongValCalls =
            couplings.stream()
                     .filter(mc -> mc.getTarget().getMethodName().equals("getFooLongVal"))
                     .collect(Collectors.toList());

        assertThat(getFooLongValCalls).isNotEmpty();
        for (MethodCoupling call : getFooLongValCalls) {
            assertThat(call.getSource().getClassName())
                .isEqualTo(MySource.class.getName());
            assertThat(call.getSource().getMethodName())
                .contains("doC")
                .contains("lambda");
        }

    }
}
