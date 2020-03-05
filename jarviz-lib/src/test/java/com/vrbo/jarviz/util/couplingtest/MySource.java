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

package com.vrbo.jarviz.util.couplingtest;

import java.util.List;

public class MySource {

    private static int MY_STATIC_BAR_VALUE = new Bar(1).getVal();

    private int myInstanceBarValue = new Bar(2).getVal();

    public int doA1() {
        return new Foo().getFooIntVal("100");
    }

    public int doA2() {
        return new Foo().getFooIntVal("200");
    }

    public int doA3() {
        return new Foo().getFooIntVal("300");
    }

    public int doB1() {
        return new Bar(3).getVal();
    }

    public int doB2() {
        return new Foo().getFooBarVal("200").getVal();
    }

    public long doC(final List<String> values) {
        return values.stream()
                     .filter(a -> !a.isEmpty())
                     .map(a -> new Foo().getFooLongVal(a))
                     .count();
    }
}
