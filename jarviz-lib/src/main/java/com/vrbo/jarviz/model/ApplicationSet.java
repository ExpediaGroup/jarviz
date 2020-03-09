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
import java.util.Set;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * The application set holds the entire collection of artifacts to be analyzed by Jarviz tool.
 */
@Value.Immutable
@JsonSerialize(as = ImmutableApplicationSet.class)
@JsonDeserialize(as = ImmutableApplicationSet.class)
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public interface ApplicationSet {

    /**
     * Optional name of the application set.
     *
     * @return The application set name.
     */
    Optional<String> getAppSetName();

    /**
     * The list of applications belongs to the application set.
     *
     * @return The set of applications name.
     */
    Set<Application> getApplications();

    /**
     * Performs validation at build time
     */
    @Value.Check
    default void check() {
        if (getAppSetName().isPresent()) {
            final String name = getAppSetName().get();
            checkArgument(!name.isEmpty(), "appSetName should not be empty");
        }
        checkArgument(!getApplications().isEmpty(), "applications should not be empty");
    }

    class Builder extends ImmutableApplicationSet.Builder {}
}
