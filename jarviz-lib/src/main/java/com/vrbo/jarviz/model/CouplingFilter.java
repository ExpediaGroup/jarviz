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
import java.util.concurrent.Callable;
import java.util.regex.Pattern;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * The coupling filter specifies the RegEx patterns to filter the couplings found at analysis,
 * before generating the output data.
 */
@Value.Immutable
@JsonSerialize(as = ImmutableCouplingFilter.class)
@JsonDeserialize(as = ImmutableCouplingFilter.class)
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public interface CouplingFilter {

    /**
     * Optional RegEx pattern to match with the package name (of the source class) in the coupling.
     * e.g.:
     * To match any source package name starting with com.xyz.foo or com.xyz.bar (including sub-packages):
     * <code>"^(com\\.xyz\\.foo|com\\.xyz\\.bar).*$"</code>
     */
    Optional<String> getSourcePackage();

    /**
     * Optional RegEx pattern to match with the name of the source class in the coupling.
     * Note that this matches with only the simple class name, not the fully qualified class name.
     * For the class "com.xyz.foo.MyClass" this will math the pattern against "MyClass".
     * e.g.:
     * To match any source class name to ABC, Xyz or Hello:
     * <code>"^(ABC|Xyz|Hello)$"</code>
     */
    Optional<String> getSourceClass();

    /**
     * Optional RegEx pattern to match with the method name (of the source class) in the coupling.
     * e.g.:
     * To match a method name to getToken, setToken or isToken:
     * <code>"^(get|set|is)Token$"</code>
     */
    Optional<String> getSourceMethod();

    /**
     * Optional RegEx pattern to match with the package name (of the target class) in the coupling.
     * e.g.:
     * To exactly match any target package to com.xyz.foo or com.xyz.bar (excluding sub-packages):
     * <code>"^(com\\.xyz\\.foo|com\\.xyz\\.bar)$"</code>
     */
    Optional<String> getTargetPackage();

    /**
     * Optional RegEx pattern to match with the name of the target class in the coupling.
     * Note that this matches only the simple class name, not the fully qualified class name.
     * For the class "com.xyz.foo.MyClass" this will match the pattern against "MyClass".
     * e.g.:
     * To match any target class name to MyClass1, MyClass2, MyClass3 or MyClass4:
     * <code>"^MyClass[1-4]$"</code>
     */
    Optional<String> getTargetClass();

    /**
     * Optional RegEx pattern to match with the method name (of the target class) in the coupling.
     * e.g.:
     * To exactly match a method name to myMethod:
     * <code>"^myMethod$"</code>
     */
    Optional<String> getTargetMethod();

    @JsonIgnore
    @Value.Lazy
    default Optional<Pattern> getSourcePackagePattern() {
        return getSourcePackage().map(Pattern::compile);
    }

    @JsonIgnore
    @Value.Lazy
    default Optional<Pattern> getSourceClassPattern() {
        return getSourceClass().map(Pattern::compile);
    }

    @JsonIgnore
    @Value.Lazy
    default Optional<Pattern> getSourceMethodPattern() {
        return getSourceMethod().map(Pattern::compile);
    }

    @JsonIgnore
    @Value.Lazy
    default Optional<Pattern> getTargetPackagePattern() {
        return getTargetPackage().map(Pattern::compile);
    }

    @JsonIgnore
    @Value.Lazy
    default Optional<Pattern> getTargetClassPattern() {
        return getTargetClass().map(Pattern::compile);
    }

    @JsonIgnore
    @Value.Lazy
    default Optional<Pattern> getTargetMethodPattern() {
        return getTargetMethod().map(Pattern::compile);
    }

    /**
     * Performs validation at the build time
     */
    @Value.Check
    default void check() {
        if (getSourcePackage().isPresent()) {
            checkArgument(verifyPatternAvailable(this::getSourcePackagePattern), "sourcePackage is not a valid regex");
        }

        if (getSourceClass().isPresent()) {
            checkArgument(verifyPatternAvailable(this::getSourceClassPattern), "sourceClass is not a valid regex");
        }

        if (getSourceMethod().isPresent()) {
            checkArgument(verifyPatternAvailable(this::getSourceMethodPattern), "sourceMethod is not a valid regex");
        }

        if (getTargetPackage().isPresent()) {
            checkArgument(verifyPatternAvailable(this::getTargetPackagePattern), "targetPackage is not a valid regex");
        }

        if (getTargetClass().isPresent()) {
            checkArgument(verifyPatternAvailable(this::getTargetClassPattern), "targetClass is not a valid regex");
        }

        if (getTargetMethod().isPresent()) {
            checkArgument(verifyPatternAvailable(this::getTargetMethodPattern), "targetMethod is not a valid regex");
        }
    }

    static boolean verifyPatternAvailable(final Callable<Optional<Pattern>> callable) {
        try {
            return callable.call().isPresent();
        } catch (Exception e) {
            return false;
        }
    }

    class Builder extends ImmutableCouplingFilter.Builder {}
}
