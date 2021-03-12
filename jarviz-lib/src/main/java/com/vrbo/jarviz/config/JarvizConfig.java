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

package com.vrbo.jarviz.config;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * The configuration for the Jarviz tool.
 */
@Value.Immutable
@JsonSerialize(as = ImmutableJarvizConfig.class)
@JsonDeserialize(as = ImmutableJarvizConfig.class)
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public interface JarvizConfig {

    /**
     * This directory serves as the local cache to load the artifact files from.
     * If there is a Maven configuration specified, then this directory will be also used
     * to save the downloaded artifact files from the Maven repository.
     *
     * @return The artifact director path.
     */
    String getArtifactDirectory();

    /**
     * When processing artifacts in a given appSet, should Jarviz ignore any maven
     * resolution or download errors for any given artifact and continue processing
     * remaining ones?
     *
     * @return Should Jarviz ignore maven errors and continue processing remaining artifacts?
     */
    boolean getContinueOnMavenError();

    /**
     * Creates a default config with artifactDirectory set to "/tmp/jarviz/artifacts"
     *
     * @return The {@link JarvizConfig}.
     */
    static JarvizConfig createDefaultConfig() {
        return new Builder()
                   .artifactDirectory("/tmp/jarviz/artifacts")
                   .build();
    }

    class Builder extends ImmutableJarvizConfig.Builder {}
}
