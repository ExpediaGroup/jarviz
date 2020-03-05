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

import java.io.File;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import static com.vrbo.jarviz.util.FileReadWriteUtils.toFullPath;

public class FileReadWriteUtilsTest {

    @Test
    public void testToFullPath() {
        assertThat(toFullPath("/tmp/jarviz/jars", "fooBar123.jar"))
            .isEqualTo("/tmp/jarviz/jars" + File.separator + "fooBar123.jar");

        assertThat(toFullPath("/tmp/jarviz/jars/", "fooBar123.jar"))
            .isEqualTo("/tmp/jarviz/jars" + File.separator + "fooBar123.jar");
    }

    @Test
    public void testToFullPath_ValidationFailure() {
        final String oneLevelUp = File.separator + ".." + File.separator;

        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> toFullPath("/tmp/jarviz/jars", oneLevelUp + "fooBar123.jar"));
    }
}
