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

import java.util.List;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Represents an application which is a collection of artifacts.
 */
@Value.Immutable
@JsonSerialize(as = ImmutableApplication.class)
@JsonDeserialize(as = ImmutableApplication.class)
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public interface Application {

    /**
     * The human readable name of the application.
     * Eg: ProductService
     *
     * @return Application name.
     */
    String getAppName();

    /**
     * The list of artifacts belongs to the application.
     *
     * @return List of artifacts.
     */
    List<Artifact> getArtifacts();

    /**
     * Performs validation at build time
     */
    @Value.Check
    default void check() {
        checkArgument(!getAppName().isEmpty(), "appName should not be empty");
        checkArgument(!getArtifacts().isEmpty(), "artifacts should not be empty");
    }

    class Builder extends ImmutableApplication.Builder {}
}
