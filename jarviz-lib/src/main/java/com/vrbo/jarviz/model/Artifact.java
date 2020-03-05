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

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import static com.google.common.base.Preconditions.checkArgument;
import static com.vrbo.jarviz.model.FileValidationUtils.validFileNamePart;

/**
 * Represents an artifact that can be analyzed.
 */
@Value.Immutable
@JsonSerialize(as = ImmutableArtifact.class)
@JsonDeserialize(as = ImmutableArtifact.class)
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public interface Artifact {

    /**
     * The packaging type of the artifact, default "jar".
     * Eg: "jar" or "war"
     */
    @Value.Default
    default String getPackaging() {
        return "jar";
    }

    /**
     * Artifact id of the artifact, Eg: "foo-bar"
     */
    String getArtifactId();

    /**
     * Group id of the artifact, Eg: "com.abc.xyz"
     */
    String getGroupId();

    /**
     * Version of the artifact.
     * Eg: "1.239.1"
     */
    String getVersion();

    /**
     * The snapshot version without the build details (only applicable for snapshots).
     * Checking out a snapshot using Maven can result in a file with version containing timestamp
     * (e.g. `fooBar-1.0.1-20200708.191052-12.jar`).
     * Eg: "1.0.1-SNAPSHOT" (where version is "1.0.1-20200708.191052-12")
     */
    Optional<String> getBaseVersion();

    /**
     * Optional classifier for the artifact.
     * Eg: "src"
     */
    Optional<String> getClassifier();

    /**
     * Eg:
     * For releases: "foo-bar.239.1.jar"
     * With classifier: "foo-bar.239.1-test.jar"
     * Snapshot: "foo-bar-2.0.1-20200708.191052-38.jar"
     */
    @JsonIgnore
    default String toFileName() {
        return String.format("%s-%s%s.%s",
                             getArtifactId(),
                             getVersion(),
                             getClassifier().map(s -> "-" + s).orElse(""),
                             getPackaging());
    }

    /**
     * A string of the form groupId:artifactId:version[:packaging[:classifier]]
     * Eg:
     * Release: "com.abd.xyz:foo-bar:1.239.1:jar"
     */
    @JsonIgnore
    default String toMavenId() {
        return String.format("%s:%s:%s:%s%s",
                             getGroupId(),
                             getArtifactId(),
                             getBaseVersion().orElseGet(this::getVersion),
                             getPackaging(),
                             getClassifier().map(s -> ":" + s).orElse(""));
    }

    /**
     * Performs validation at the build time
     */
    @Value.Check
    default void check() {
        checkArgument(validFileNamePart(getPackaging()), "packaging is invalid");
        checkArgument("jar".equalsIgnoreCase(getPackaging()) || "war".equalsIgnoreCase(getPackaging()),
                      "packaging should be either 'jar' or 'war'");

        checkArgument(validFileNamePart(getArtifactId()), "artifactId is invalid");
        checkArgument(validFileNamePart(getGroupId()), "groupId is invalid");
        checkArgument(validFileNamePart(getVersion()), "version is invalid");
        if (getClassifier().isPresent()) {
            checkArgument(validFileNamePart(getClassifier().get()), "classifier is invalid");
        }

        if (getBaseVersion().isPresent()) {
            final String baseVersion = getBaseVersion().get();
            checkArgument(validFileNamePart(baseVersion), "baseVersion is invalid");
        }
    }

    class Builder extends ImmutableArtifact.Builder {}
}
