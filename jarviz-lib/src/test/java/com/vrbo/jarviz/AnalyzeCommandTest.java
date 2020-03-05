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

package com.vrbo.jarviz;

import java.time.LocalDateTime;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.assertj.core.api.AbstractCharSequenceAssert;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

import static org.assertj.core.api.Assertions.assertThat;

public class AnalyzeCommandTest {

    @Test
    public void testDisplayHelp() {
        // verify this will run without throwing any exception
        AnalyzeCommand.main(new String[] {"-h"});
    }

    @Test
    public void testToHelpString() {
        final Options options = AnalyzeCommand.createOptions();
        final AbstractCharSequenceAssert<?, String> sequenceAssert =
            assertThat(AnalyzeCommand.toHelpString(options))
                .isNotBlank()
                .containsIgnoringCase("Jarviz")
                .containsIgnoringCase("Exit status:");

        for (Option o : options.getOptions()) {
            sequenceAssert.contains("-" + o.getOpt());
            sequenceAssert.contains("--" + o.getLongOpt());
        }
    }

    @Test
    public void testGenerateFileName() {
        assertThat(AnalyzeCommand.generateFileName("helloWorld",
                                                   LocalDateTime.of(2020, 8, 27, 15, 33, 56, 987_000_000),
                                                   "fooBar"))
            .isEqualTo("helloWorld_20200827_153356.fooBar");
    }

    @Test
    public void testToPaddedStrings() {
        assertThat(AnalyzeCommand.toPaddedStrings(ImmutableList.of()))
            .containsExactly();

        assertThat(AnalyzeCommand.toPaddedStrings(ImmutableList.of("")))
            .containsExactly("");

        assertThat(AnalyzeCommand.toPaddedStrings(ImmutableList.of("", "  ")))
            .containsExactly("  ", "  ");

        assertThat(AnalyzeCommand.toPaddedStrings(ImmutableList.of("abc", "")))
            .containsExactly("abc", "   ");

        assertThat(AnalyzeCommand.toPaddedStrings(ImmutableList.of("ab", "CD", "eF")))
            .containsExactly("ab", "CD", "eF");

        assertThat(AnalyzeCommand.toPaddedStrings(ImmutableList.of("ABC", "defg", "HiJ Kl", "mn")))
            .containsExactly("ABC   ", "defg  ", "HiJ Kl", "mn    ");
    }
}
