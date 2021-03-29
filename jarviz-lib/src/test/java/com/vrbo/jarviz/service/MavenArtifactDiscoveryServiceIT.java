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

package com.vrbo.jarviz.service;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import com.vrbo.jarviz.config.JarvizConfig;
import com.vrbo.jarviz.model.Artifact;

import static org.assertj.core.api.Assertions.assertThat;

public class MavenArtifactDiscoveryServiceIT {

    private MavenArtifactDiscoveryService artifactDiscoveryService;

    @Before
    public void setup() {
        artifactDiscoveryService = new MavenArtifactDiscoveryService(
            new JarvizConfig.Builder()
                .artifactDirectory("/tmp/jarviz/artifacts")
                .continueOnMavenError(false)
                .build());
    }

    @Test
    public void testDiscoverArtifact_Success() throws ArtifactNotFoundException {
        final Artifact artifact = new Artifact.Builder()
                                      .groupId("org.ow2.asm")
                                      .artifactId("asm")
                                      .version("7.1")
                                      .build();
        final File file = artifactDiscoveryService.discoverArtifact(artifact);
        assertThat(file).exists();
    }

    @Test(expected = ArtifactNotFoundException.class)
    public void testDiscoverArtifact_Fail() throws ArtifactNotFoundException {
        final Artifact artifact = new Artifact.Builder()
                                      .groupId("__my_invalid_group__")
                                      .artifactId("__my_invalid_artifact__")
                                      .version("0")
                                      .build();
        artifactDiscoveryService.discoverArtifact(artifact);
    }

    @Test
    public void testDiscoverArtifact_WhenContinueOnMavenError_IsTrue() throws ArtifactNotFoundException {
        final MavenArtifactDiscoveryService artifactDiscoveryService2 = new MavenArtifactDiscoveryService(
            new JarvizConfig.Builder()
                .artifactDirectory("/tmp/jarviz/artifacts")
                .continueOnMavenError(true)
                .build());
        final Artifact artifact = new Artifact.Builder()
                                        .groupId("__my_invalid_group__")
                                        .artifactId("__my_invalid_artifact__")
                                        .version("0")
                                        .build();
        final File file = artifactDiscoveryService2.discoverArtifact(artifact);
        assertThat(file).doesNotExist();
    }
}
