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

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.immutables.value.Value;
import org.junit.Test;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import static org.assertj.core.api.Assertions.assertThat;

import static com.vrbo.jarviz.util.JsonUtils.fromJsonString;
import static com.vrbo.jarviz.util.JsonUtils.toJsonString;

public class JsonUtilsTest {

    @Test
    public void jsonRoundTripTest() {
        final TestFooClass testFooClass =
            new TestFooClass.Builder()
                .name("Hello World!")
                .number(731946825)
                .trueOrNot(true)
                .bar(new TestBarClass.Builder()
                         .id(UUID.fromString("f90ad4f8-beef-cafe-feed-7898d9e629a5"))
                         .type("Food")
                         .dateTime(LocalDateTime.of(2020, 7, 11, 12, 35, 46))
                         .build())
                .build();

        assertThat(fromJsonString(toJsonString(testFooClass), TestFooClass.class))
            .isEqualTo(testFooClass);
    }

    @Value.Immutable
    @JsonSerialize(as = ImmutableTestFooClass.class)
    @JsonDeserialize(as = ImmutableTestFooClass.class)
    interface TestFooClass {

        String getName();

        int getNumber();

        boolean getTrueOrNot();

        TestBarClass getBar();

        class Builder extends ImmutableTestFooClass.Builder {}
    }

    @Value.Immutable
    @JsonSerialize(as = ImmutableTestBarClass.class)
    @JsonDeserialize(as = ImmutableTestBarClass.class)
    interface TestBarClass {

        UUID getId();

        Optional<String> getType();

        LocalDateTime getDateTime();

        class Builder extends ImmutableTestBarClass.Builder {}
    }
}
