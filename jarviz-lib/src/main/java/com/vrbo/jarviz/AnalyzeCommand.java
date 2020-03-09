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

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import com.google.common.base.Strings;
import com.vrbo.jarviz.config.CouplingFilterConfig;
import com.vrbo.jarviz.config.JarvizConfig;
import com.vrbo.jarviz.model.ApplicationSet;
import com.vrbo.jarviz.service.CouplingAnalyser;

import static com.vrbo.jarviz.util.FileReadWriteUtils.readFileAsString;
import static com.vrbo.jarviz.util.JsonUtils.fromJsonString;

/**
 * Implements analyze command for Javiz.
 */
public class AnalyzeCommand {

    interface ExitStatus {

        int CLI_INIT_FAILED = 1;
        int ANALYSER_FAILED = 2;
    }

    interface Params {

        String ARTIFACTS_LONG = "artifacts";
        String ARTIFACTS_SHORT = "a";
        String CONFIG_LONG = "config";
        String CONFIG_SHORT = "c";
        String FILTER_LONG = "filter";
        String FILTER_SHORT = "f";
        String OUTPUT_LONG = "output";
        String OUTPUT_SHORT = "o";
        String HELP_LONG = "help";
        String HELP_SHORT = "h";
        String VERSION_LONG = "version";
        String VERSION_SHORT = "v";
    }

    // e.g.: jarviz_result_20200827_153356.jsonl
    private static final String DEFAULT_OUTPUT = generateFileName("jarviz_result", LocalDateTime.now(), "jsonl");

    private static final Options OPTIONS = createOptions();

    static Options createOptions() {
        return new Options()
                   .addOption(Option.builder(Params.CONFIG_SHORT)
                                    .longOpt(Params.CONFIG_LONG)
                                    .desc("Path to the configurations file")
                                    .hasArg()
                                    .build())
                   .addOption(Option.builder(Params.ARTIFACTS_SHORT)
                                    .longOpt(Params.ARTIFACTS_LONG)
                                    .desc("Path to the file containing list of artifacts")
                                    .hasArg()
                                    .build())
                   .addOption(Option.builder(Params.FILTER_SHORT)
                                    .longOpt(Params.FILTER_LONG)
                                    .desc("Path to the coupling filter configuration file")
                                    .hasArg()
                                    .build())
                   .addOption(Option.builder(Params.OUTPUT_SHORT)
                                    .longOpt(Params.OUTPUT_LONG)
                                    .desc("Path to the newline-delimited JSON output file (.jsonl)")
                                    .hasArg()
                                    .build())
                   .addOption(Option.builder(Params.HELP_SHORT)
                                    .longOpt(Params.HELP_LONG)
                                    .desc("Display help")
                                    .build())
                   .addOption(Option.builder(Params.VERSION_SHORT)
                                    .longOpt(Params.VERSION_LONG)
                                    .desc("Display version")
                                    .build());
    }

    public static void main(String[] args) {
        final CommandLineParser parser = new DefaultParser();

        final CommandLine line;
        try {
            line = parser.parse(OPTIONS, args);
        } catch (Exception e) {
            reportError(e);
            System.exit(ExitStatus.CLI_INIT_FAILED);
            return;
        }

        // help
        if (line.hasOption(Params.HELP_LONG)) {
            System.out.println(toHelpString(OPTIONS));
            return;
        }

        // version
        if (line.hasOption(Params.VERSION_LONG)) {
            System.out.println(findImplementationVersion().orElse(""));
            return;
        }

        // other params
        final JarvizConfig jarvizConfig;
        final ApplicationSet applicationSet;
        final CouplingFilterConfig filterConfig;
        final String reportFile;
        try {
            final File configFile = loadFileFromArgument(line, Params.CONFIG_LONG, false);
            final File artifactsFile = loadFileFromArgument(line, Params.ARTIFACTS_LONG, true);
            final File filterFile = loadFileFromArgument(line, Params.FILTER_LONG, true);
            final String output = loadStringFromArgument(line, Params.OUTPUT_LONG);

            jarvizConfig = configFile != null ? fromJsonString(readFileAsString(configFile), JarvizConfig.class) : null;
            applicationSet = fromJsonString(readFileAsString(artifactsFile), ApplicationSet.class);
            filterConfig = fromJsonString(readFileAsString(filterFile), CouplingFilterConfig.class);
            reportFile = (output == null || output.isEmpty()) ? DEFAULT_OUTPUT : output;
        } catch (Exception e) {
            reportError(e);
            System.exit(ExitStatus.CLI_INIT_FAILED);
            return;
        }

        try {
            final CouplingAnalyser analyser = new CouplingAnalyser();
            analyser.start(jarvizConfig, applicationSet, filterConfig, reportFile);
        } catch (Exception e) {
            reportError(e);
            System.exit(ExitStatus.ANALYSER_FAILED);
        }
    }

    private static void reportError(final Exception e) {
        if (e.getMessage() == null) {
            System.err.println("Analyze failed");
            e.printStackTrace();
        } else {
            System.err.println("Analyze failed: " + e.getMessage());
        }
    }

    private static File loadFileFromArgument(final CommandLine line, final String argumentName, final boolean required) {
        final String argumentValue = loadStringFromArgument(line, argumentName);
        if (argumentValue == null || argumentValue.isEmpty()) {
            if (required) {
                throw new IllegalArgumentException(String.format("argument '%s' is missing", argumentName));
            } else {
                return null;
            }
        }

        final File file = new File(argumentValue);
        if (!file.exists()) {
            if (required) {
                throw new IllegalArgumentException(String.format("file '%s' does not exist", argumentValue));
            } else {
                return null;
            }
        }

        return file;
    }

    private static String loadStringFromArgument(final CommandLine line, final String argumentName) {
        if (line.hasOption(argumentName)) {
            final String[] arguments = line.getOptionValues(argumentName);
            if (arguments.length > 1) {
                throw new IllegalArgumentException(String.format("argument '%s' does not support multiple values", argumentName));
            }

            return arguments[0];
        }

        return null;
    }

    static String toHelpString(final Options options) {
        final StringBuilder b = new StringBuilder();
        b.append("Jarviz Dependency Analyser\n");
        b.append("--------------------------\n");
        b.append("Jarviz is a dependency analysis tool for Java and it can generate a couplings graph between selected methods.\n");
        b.append("It analyzes the bytecode from Java artifacts to find out the dependencies. The generated couplings graph\n");
        b.append("is saved in to a newline-delimited JSON file as the output.\n");
        b.append('\n');

        b.append("Analyze Command\n");
        b.append("---------------\n");

        b.append("The following options are available:\n");

        final List<Option> optionList = new ArrayList<>(options.getOptions());
        final List<String> paddedParams = toPaddedStrings(
            optionList.stream()
                      .map(o -> String.format("-%s, --%s %s", o.getOpt(), o.getLongOpt(), o.hasArg() ? " <arg>" : ""))
                      .collect(Collectors.toList())
        );
        for (int i = 0; i < optionList.size(); i++) {
            final Option o = optionList.get(i);
            b.append(' ').append(paddedParams.get(i)).append("    ").append(o.getDescription()).append('\n');
        }

        b.append('\n');
        b.append("Exit status:\n");
        b.append(" 0    if successful\n");
        b.append(String.format(" %d    if cli initialization failed\n", ExitStatus.CLI_INIT_FAILED));
        b.append(String.format(" %d    if analyser failed\n", ExitStatus.ANALYSER_FAILED));

        findImplementationVersion().ifPresent(v -> b.append(String.format("\nVersion: %s\n", v)));

        return b.toString();
    }

    private static Optional<String> findImplementationVersion() {
        return Optional.ofNullable(AnalyzeCommand.class.getPackage().getImplementationVersion());
    }

    static List<String> toPaddedStrings(final List<String> strings) {
        final int maxLen = strings.stream()
                                  .map(String::length)
                                  .max(Integer::compareTo)
                                  .orElse(0);
        return strings.stream()
                      .map(s -> Strings.padEnd(s, maxLen, ' '))
                      .collect(Collectors.toList());
    }

    static String generateFileName(@Nonnull final String prefix,
                                   @Nonnull final LocalDateTime dt,
                                   @Nonnull final String extension) {
        return prefix +
               "_" +
               dt.format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) +
               "." +
               extension;
    }
}
