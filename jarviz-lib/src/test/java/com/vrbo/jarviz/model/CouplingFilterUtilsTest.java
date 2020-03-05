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

import java.util.Optional;
import java.util.regex.Pattern;

import org.junit.Test;

import com.vrbo.jarviz.config.CouplingFilterConfig;

import static org.assertj.core.api.Assertions.assertThat;

import static com.vrbo.jarviz.model.CouplingFilterUtils.filterMethodCoupling;
import static com.vrbo.jarviz.model.CouplingFilterUtils.matchCoupling;
import static com.vrbo.jarviz.model.CouplingFilterUtils.matchString;

public class CouplingFilterUtilsTest {

    private static final MethodCoupling DEFAULT_COUPLING =
        new MethodCoupling.Builder()
            .source(new Method.Builder()
                        .className("com.foo.MySourceClass")
                        .methodName("iAmCallingYou")
                        .build())
            .target(new Method.Builder()
                        .className("com.bar.MyTargetClass")
                        .methodName("whyDoYouCallMe")
                        .build())
            .build();

    @Test
    public void testFilterMethodCoupling() {
        assertThat(filterMethodCoupling(new CouplingFilterConfig.Builder().build(), DEFAULT_COUPLING)).isTrue();

        assertThat(filterMethodCoupling(new CouplingFilterConfig.Builder()
                                            .include(new CouplingFilter.Builder()
                                                         .sourcePackage("^com\\.foo$")
                                                         .targetPackage("^com\\.bar$")
                                                         .build())
                                            .build(),
                                        DEFAULT_COUPLING)).isTrue();
        assertThat(filterMethodCoupling(new CouplingFilterConfig.Builder()
                                            .exclude(new CouplingFilter.Builder()
                                                         .sourcePackage("^com\\.bar$")
                                                         .targetPackage("^com\\.foo$")
                                                         .build())
                                            .build(),
                                        DEFAULT_COUPLING)).isTrue();

        assertThat(filterMethodCoupling(new CouplingFilterConfig.Builder()
                                            .exclude(new CouplingFilter.Builder()
                                                         .sourcePackage("^com\\.foo$")
                                                         .targetPackage("^com\\.bar$")
                                                         .build())
                                            .build(),
                                        DEFAULT_COUPLING)).isFalse();
        assertThat(filterMethodCoupling(new CouplingFilterConfig.Builder()
                                            .include(new CouplingFilter.Builder()
                                                         .sourcePackage("^com\\.bar$")
                                                         .targetPackage("^com\\.foo$")
                                                         .build())
                                            .build(),
                                        DEFAULT_COUPLING)).isFalse();

        assertThat(filterMethodCoupling(new CouplingFilterConfig.Builder()
                                            .include(new CouplingFilter.Builder()
                                                         .sourcePackage("^[\\w\\.]+$")
                                                         .build())
                                            .exclude(new CouplingFilter.Builder()
                                                         .targetPackage("^[\\d\\.]+$")
                                                         .build())
                                            .build(),
                                        DEFAULT_COUPLING)).isTrue();

        assertThat(filterMethodCoupling(new CouplingFilterConfig.Builder()
                                            .include(new CouplingFilter.Builder()
                                                         .sourcePackage("^[\\d\\.]+$")
                                                         .build())
                                            .exclude(new CouplingFilter.Builder()
                                                         .targetPackage("^[\\w\\.]+$")
                                                         .build())
                                            .build(),
                                        DEFAULT_COUPLING)).isFalse();
        assertThat(filterMethodCoupling(new CouplingFilterConfig.Builder()
                                            .include(new CouplingFilter.Builder()
                                                         .sourcePackage("^[\\w\\.]+$")
                                                         .build())
                                            .exclude(new CouplingFilter.Builder()
                                                         .targetPackage("^[\\w\\.]+$")
                                                         .build())
                                            .build(),
                                        DEFAULT_COUPLING)).isFalse();
    }

    @Test
    public void testMatchCoupling() {
        assertThat(matchCoupling(new CouplingFilter.Builder().build(), DEFAULT_COUPLING)).isTrue();

        assertThat(matchCoupling(new CouplingFilter.Builder()
                                     .sourcePackage("[\\w\\.]+")
                                     .sourceClass("\\w+")
                                     .sourceMethod("\\w+")
                                     .targetPackage("[\\w\\.]+")
                                     .targetClass("\\w+")
                                     .targetMethod("\\w+")
                                     .build(),
                                 DEFAULT_COUPLING)).isTrue();

        assertThat(matchCoupling(new CouplingFilter.Builder()
                                     .sourcePackage("\\d+")
                                     .sourceClass("\\d+")
                                     .sourceMethod("\\d+")
                                     .targetPackage("\\d+")
                                     .targetClass("\\d+")
                                     .targetMethod("\\d+")
                                     .build(),
                                 DEFAULT_COUPLING)).isFalse();
    }

    @Test
    public void testMatchCoupling_sourcePackage() {
        assertThat(matchCoupling(new CouplingFilter.Builder().sourcePackage("[\\w\\.]+").build(), DEFAULT_COUPLING)).isTrue();
        assertThat(matchCoupling(new CouplingFilter.Builder().sourcePackage("\\d+").build(), DEFAULT_COUPLING)).isFalse();
    }

    @Test
    public void testMatchCoupling_sourceClass() {
        assertThat(matchCoupling(new CouplingFilter.Builder().sourceClass("\\w+").build(), DEFAULT_COUPLING)).isTrue();
        assertThat(matchCoupling(new CouplingFilter.Builder().sourceClass("\\d+").build(), DEFAULT_COUPLING)).isFalse();
    }

    @Test
    public void testMatchCoupling_sourceMethod() {
        assertThat(matchCoupling(new CouplingFilter.Builder().sourceMethod("\\w+").build(), DEFAULT_COUPLING)).isTrue();
        assertThat(matchCoupling(new CouplingFilter.Builder().sourceMethod("\\d+").build(), DEFAULT_COUPLING)).isFalse();
    }

    @Test
    public void testMatchCoupling_targetPackage() {
        assertThat(matchCoupling(new CouplingFilter.Builder().targetPackage("[\\w\\.]+").build(), DEFAULT_COUPLING)).isTrue();
        assertThat(matchCoupling(new CouplingFilter.Builder().targetPackage("\\d+").build(), DEFAULT_COUPLING)).isFalse();
    }

    @Test
    public void testMatchCoupling_targetClass() {
        assertThat(matchCoupling(new CouplingFilter.Builder().targetClass("\\w+").build(), DEFAULT_COUPLING)).isTrue();
        assertThat(matchCoupling(new CouplingFilter.Builder().targetClass("\\d+").build(), DEFAULT_COUPLING)).isFalse();
    }

    @Test
    public void testMatchCoupling_targetMethod() {
        assertThat(matchCoupling(new CouplingFilter.Builder().targetMethod("\\w+").build(), DEFAULT_COUPLING)).isTrue();
        assertThat(matchCoupling(new CouplingFilter.Builder().targetMethod("\\d+").build(), DEFAULT_COUPLING)).isFalse();
    }

    @Test
    public void testMatchString() {
        assertThat(matchString(Optional.empty(), "fooBar")).isTrue();
        assertThat(matchString(Optional.of(Pattern.compile("\\w+")), "fooBar")).isTrue();
        assertThat(matchString(Optional.of(Pattern.compile("\\d+")), "fooBar")).isFalse();
    }
}
