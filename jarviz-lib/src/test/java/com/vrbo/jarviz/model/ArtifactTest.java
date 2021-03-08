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

import java.io.File;
import java.util.Optional;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class ArtifactTest {

    final Artifact releaseArtifact =
        new Artifact.Builder()
            .artifactId("api-service")
            .groupId("com.vrbo.api")
            .version("1.23.1")
            .build();

    final Artifact snapshotArtifact =
        new Artifact.Builder()
            .artifactId("foo-bar")
            .groupId("abc.xyz")
            .version("2.0.1-20200708.191052-38")
            .baseVersion("2.0.1-SNAPSHOT")
            .build();

    @Test
    public void testToFileName() {

        assertThat(releaseArtifact.toFileName())
            .isEqualTo("api-service-1.23.1.jar");

        assertThat(new Artifact.Builder()
                       .from(releaseArtifact)
                       .classifier("logic")
                       .build()
                       .toFileName())
            .isEqualTo("api-service-1.23.1-logic.jar");

        assertThat(new Artifact.Builder()
                       .from(releaseArtifact)
                       .packaging("war")
                       .build()
                       .toFileName())
            .isEqualTo("api-service-1.23.1.war");

        assertThat(snapshotArtifact.toFileName())
            .isEqualTo("foo-bar-2.0.1-20200708.191052-38.jar");

        assertThat(new Artifact.Builder()
                       .from(snapshotArtifact)
                       .version("2.0.1-SNAPSHOT")
                       .baseVersion(Optional.empty())
                       .build()
                       .toFileName())
            .isEqualTo("foo-bar-2.0.1-SNAPSHOT.jar");
    }

    @Test
    public void testToFileName_WhenVersionIsUnspecified() {

        final Artifact releaseVerArtifact =
            new Artifact.Builder()
                .artifactId("api-service")
                .groupId("com.vrbo.api")
                .version("RELEASE")
                .build();

        assertThat(releaseVerArtifact.toFileName())
            .isEqualTo("api-service.jar");

        assertThat(new Artifact.Builder()
                        .from(releaseVerArtifact)
                        .classifier("logic")
                        .build()
                        .toFileName())
                        .isEqualTo("api-service.jar");

        assertThat(new Artifact.Builder()
                        .from(releaseVerArtifact)
                        .packaging("war")
                        .build()
                        .toFileName())
                        .isEqualTo("api-service.war");

        final Artifact latestVerArtifact =
            new Artifact.Builder()
                .artifactId("foo-bar")
                .groupId("abc.xyz")
                .version("LATEST")
                .build();

        assertThat(latestVerArtifact.toFileName())
            .isEqualTo("foo-bar.jar");

        assertThat(new Artifact.Builder()
                    .from(latestVerArtifact)
                    .version("LATEST")
                    .baseVersion(Optional.empty())
                    .build()
                    .toFileName())
                    .isEqualTo("foo-bar.jar");
    }

    @Test
    public void testToMavenId() {

        assertThat(releaseArtifact.toMavenId())
            .isEqualTo("com.vrbo.api:api-service:1.23.1:jar");

        assertThat(new Artifact.Builder()
                       .from(releaseArtifact)
                       .classifier("logic")
                       .build()
                       .toMavenId())
            .isEqualTo("com.vrbo.api:api-service:1.23.1:jar:logic");

        assertThat(new Artifact.Builder()
                       .from(releaseArtifact)
                       .packaging("war")
                       .build()
                       .toMavenId())
            .isEqualTo("com.vrbo.api:api-service:1.23.1:war");

        assertThat(snapshotArtifact.toMavenId())
    .isEqualTo("abc.xyz:foo-bar:2.0.1-SNAPSHOT:jar");

        assertThat(new Artifact.Builder()
                       .from(snapshotArtifact)
                       .version("2.0.1-SNAPSHOT")
                       .baseVersion(Optional.empty())
                       .build()
                       .toMavenId())
            .isEqualTo("abc.xyz:foo-bar:2.0.1-SNAPSHOT:jar");
    }

    @Test
    public void testValidation() {
        assertValidationFail("packaging",
                             new Artifact.Builder()
                                 .from(snapshotArtifact)
                                 .packaging("pom"));

        final String invalidStringWithPathSeparator = ".." + File.separator + "myfile";
        assertValidationFail("artifactId",
                             new Artifact.Builder()
                                 .from(snapshotArtifact)
                                 .artifactId(invalidStringWithPathSeparator));
        assertValidationFail("groupId",
                             new Artifact.Builder()
                                 .from(snapshotArtifact)
                                 .groupId(invalidStringWithPathSeparator));
        assertValidationFail("version",
                             new Artifact.Builder()
                                 .from(snapshotArtifact)
                                 .version(invalidStringWithPathSeparator));
        assertValidationFail("baseVersion",
                             new Artifact.Builder()
                                 .from(snapshotArtifact)
                                 .baseVersion(invalidStringWithPathSeparator));
        assertValidationFail("classifier",
                             new Artifact.Builder()
                                 .from(snapshotArtifact)
                                 .classifier(invalidStringWithPathSeparator));
        assertValidationFail("packaging",
                             new Artifact.Builder()
                                 .from(snapshotArtifact)
                                 .packaging(invalidStringWithPathSeparator));
    }

    private void assertValidationFail(final String field, final Artifact.Builder builder) {
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(builder::build)
            .withMessageContaining(field);
    }
}
