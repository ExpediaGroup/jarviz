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

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@JsonSerialize(as = ImmutableCouplingRecord.class)
@JsonDeserialize(as = ImmutableCouplingRecord.class)
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public interface CouplingRecord {

    String getAppSetName();

    /**
     * The human readable name of the application.
     *
     * @return The application name.
     */
    String getApplicationName();

    /**
     * The file name of the artifact (e.g. "foo-product-1.2.1.jar").
     *
     * @return The artifact file name.
     */
    String getArtifactFileName();

    /**
     * The id of the artifact (e.g. "foo-product").
     *
     * @return The artifact id.
     */
    String getArtifactId();

    /**
     * Group id of the artifact (e.g. "com.foo.bar").
     *
     * @return The artifact group.
     */
    String getArtifactGroup();

    /**
     * The version of the artifact (e.g. "1.2.0" or "1.2.1-SNAPSHOT").
     *
     * @return The artifact version.
     */
    String getArtifactVersion();

    /**
     * The fully qualified name of the source class in the coupling.
     *
     * @return The source class name.
     */
    String getSourceClass();

    /**
     * The method name (of the source class) in the coupling.
     *
     * @return The source method name.
     */
    String getSourceMethod();

    /**
     * The fully qualified name of the target class in the coupling.
     *
     * @return The target class name.
     */
    String getTargetClass();

    /**
     * The method name (of the target class) in the coupling.
     *
     * @return The target method name.
     */
    String getTargetMethod();

    class Builder extends ImmutableCouplingRecord.Builder {}
}
