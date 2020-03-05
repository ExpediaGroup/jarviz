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

package com.vrbo.jarviz.service;

import java.io.IOException;

import org.junit.Test;

import com.google.common.reflect.ClassPath;
import com.vrbo.jarviz.model.ShadowClass;
import com.vrbo.jarviz.util.classloadertest.MyTestClass1;

import static org.assertj.core.api.Assertions.assertThat;

import static com.vrbo.jarviz.service.JarClassLoaderService.fileNameToFileProtocol;
import static com.vrbo.jarviz.service.JarClassLoaderService.fileNameToJarPrefix;
import static com.vrbo.jarviz.service.JarClassLoaderService.mapClassInfoToShadowClass;

public class JarClassLoaderServiceTest {

    @Test
    public void testMapClassInfoToShadowClass() throws IOException {
        final ClassPath.ClassInfo classInfo =
            ClassPath.from(Thread.currentThread().getContextClassLoader())
                     .getTopLevelClasses("com.vrbo.jarviz.util.classloadertest")
                     .stream()
                     .filter(c -> MyTestClass1.class.getName().equals(c.getName()))
                     .findAny()
                     .orElseThrow(IllegalArgumentException::new);

        final ShadowClass expectedShadowClass =
            new ShadowClass.Builder()
                .className(classInfo.getName())
                .classBytes(classInfo.asByteSource().read())
                .build();

        assertThat(mapClassInfoToShadowClass(classInfo)).isEqualTo(expectedShadowClass);
    }

    @Test
    public void testFileNameToFileProtocol() {
        assertThat(fileNameToFileProtocol("fooBar")).isEqualTo("file:fooBar");
        assertThat(fileNameToFileProtocol("file:fooBar")).isEqualTo("file:fooBar");
    }

    @Test
    public void testFileNameToJarPrefix() {
        assertThat(fileNameToJarPrefix("fooBar")).isEqualTo("jar:file:fooBar!");
        assertThat(fileNameToJarPrefix("file:fooBar")).isEqualTo("jar:file:fooBar!");
    }
}
