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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.vrbo.jarviz.config.JarvizConfig;
import com.vrbo.jarviz.model.Artifact;

import static com.vrbo.jarviz.util.FileReadWriteUtils.toFullPath;

public class MavenArtifactDiscoveryService implements ArtifactDiscoveryService {

    private final Logger log = LoggerFactory.getLogger(MavenArtifactDiscoveryService.class);

    private final String localRepoPath;

    @Inject
    public MavenArtifactDiscoveryService(final JarvizConfig config) {
        this.localRepoPath = config.getArtifactDirectory();
    }

    @Override
    public File discoverArtifact(final Artifact artifact) throws ArtifactNotFoundException {
        final File file = new File(toFullPath(localRepoPath, artifact.toFileName()));
        if (!file.exists()) {
            runMavenCopy(artifact);
        }

        return file;
    }

    private Process runMavenCopy(final Artifact artifact) throws ArtifactNotFoundException {
        try {
            final String artifactMavenId = artifact.toMavenId();
            log.info("Maven: fetching artifact {}", artifactMavenId);
            final String mvnCommand = String.format("mvn dependency:copy -DoutputDirectory=%s -Dartifact=%s",
                                                    localRepoPath, artifactMavenId);
            final Process process = Runtime.getRuntime().exec(mvnCommand);
            int exitCode = process.waitFor();

            if (exitCode != 0) {
                // In case, there is an error, let's log and throw exception
                drainInputStream(process.getInputStream()).forEach(s -> log.info("{}", s));
                drainInputStream(process.getErrorStream()).forEach(s -> log.error("{}", s));
                log.info("Maven command failed: {}", mvnCommand);
                throw new ArtifactNotFoundException(
                    String.format("Unable to fetch the artifact %s from Maven repository", artifactMavenId));
            }

            return process;
        } catch (Exception e) {
            throw new ArtifactNotFoundException(e);
        }
    }

    private static List<String> drainInputStream(final InputStream stream) throws IOException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        final ImmutableList.Builder<String> logLines = ImmutableList.builder();
        String s;
        while ((s = reader.readLine()) != null) {
            logLines.add(s);
        }
        return logLines.build();
    }
}
