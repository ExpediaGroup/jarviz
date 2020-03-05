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

package com.vrbo.jarviz.analyser;

import org.junit.Test;

import com.vrbo.jarviz.model.CouplingFilter;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class CouplingFilterTest {

    private static final String INVALID_REGEX = "\\";

    @Test
    public void getSourcePackagePattern() {
        assertValidationFail("sourcePackage", new CouplingFilter.Builder().sourcePackage(INVALID_REGEX));
    }

    @Test
    public void getSourceClassPattern() {
        assertValidationFail("sourceClass", new CouplingFilter.Builder().sourceClass(INVALID_REGEX));
    }

    @Test
    public void getSourceMethodPattern() {
        assertValidationFail("sourceMethod", new CouplingFilter.Builder().sourceMethod(INVALID_REGEX));
    }

    @Test
    public void getTargetPackagePattern() {
        assertValidationFail("targetPackage", new CouplingFilter.Builder().targetPackage(INVALID_REGEX));
    }

    @Test
    public void getTargetClassPattern() {
        assertValidationFail("targetClass", new CouplingFilter.Builder().targetClass(INVALID_REGEX));
    }

    @Test
    public void getTargetMethodPattern() {
        assertValidationFail("targetMethod", new CouplingFilter.Builder().targetMethod(INVALID_REGEX));
    }

    private void assertValidationFail(final String field, final CouplingFilter.Builder builder) {
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(builder::build)
            .withMessageContaining(field);
    }
}
