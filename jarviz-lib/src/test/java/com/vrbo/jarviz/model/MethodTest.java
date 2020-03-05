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

package com.vrbo.jarviz.model;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MethodTest {

    @Test
    public void testGetSimpleClassName() {
        final Method method1 = new Method.Builder()
                                  .methodName("fooMethod")
                                  .className("com.foo.bar.MyFooClass")
                                  .build();
        assertThat(method1.getSimpleClassName()).isEqualTo("MyFooClass");

        final Method method2 = new Method.Builder()
                                   .methodName("fooMethod")
                                   .className("MyFooClass")
                                   .build();
        assertThat(method1.getSimpleClassName()).isEqualTo("MyFooClass");
    }

    @Test
    public void testGetPackageName() {
        final Method method1 = new Method.Builder()
                                  .methodName("fooMethod")
                                  .className("com.foo.bar.MyFooClass")
                                  .build();
        assertThat(method1.getPackageName()).isEqualTo("com.foo.bar");

        final Method method2 = new Method.Builder()
                                   .methodName("fooMethod")
                                   .className("MyFooClass")
                                   .build();
        assertThat(method2.getPackageName()).isEqualTo("");
    }

    @Test
    public void testToStringShort() {
        final Method method = new Method.Builder()
                                  .methodName("fooMethod")
                                  .className("com.foo.bar.MyFooClass")
                                  .build();
        assertThat(method.toStringShort()).isEqualTo("com.foo.bar.MyFooClass#fooMethod");
    }
}
