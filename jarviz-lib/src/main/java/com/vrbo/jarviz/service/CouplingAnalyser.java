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

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.glassfish.hk2.api.ServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vrbo.jarviz.config.CouplingFilterConfig;
import com.vrbo.jarviz.config.JarvizConfig;
import com.vrbo.jarviz.model.Application;
import com.vrbo.jarviz.model.ApplicationSet;
import com.vrbo.jarviz.model.Artifact;
import com.vrbo.jarviz.model.CouplingRecord;
import com.vrbo.jarviz.model.MethodCoupling;
import com.vrbo.jarviz.model.ShadowClass;
import com.vrbo.jarviz.visitor.FilteredClassVisitor;

import static com.vrbo.jarviz.util.FileReadWriteUtils.getOrCreateDirectory;

public class CouplingAnalyser {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final AtomicInteger applicationSetClassCount = new AtomicInteger();

    private final AtomicInteger applicationClassCount = new AtomicInteger();

    /**
     * Start the analyser and return a list of {@link MethodCoupling}s.
     *
     * @param jarvizConfig   The configurations.
     * @param applicationSet The application set.
     * @param filterConfig   The filters.
     * @param reportFile     File name for the report.
     */
    public void start(final JarvizConfig jarvizConfig,
                      final ApplicationSet applicationSet,
                      final CouplingFilterConfig filterConfig,
                      final String reportFile) {
        final JarvizConfig config;
        if (jarvizConfig != null) {
            config = jarvizConfig;
        } else {
            log.info("JarvizConfig is not found, using default config.");
            config = new JarvizConfig.Builder().build();
        }

        init(config);

        final ServiceLocator serviceLocator = JarvizServiceLocator.createServiceLocator(config);
        final ClassLoaderService classLoaderService = serviceLocator.getService(ClassLoaderService.class);

        log.info("ApplicationSet found:\n{}", applicationSetToString(applicationSet));

        analyzeApplicationSet(applicationSet, filterConfig, classLoaderService, reportFile);
    }

    private void init(final JarvizConfig jarvizConfig) {
        log.info("Initializing analyser");
        final String dir = jarvizConfig.getArtifactDirectory();
        log.info("Artifact directory: {}", dir);
        if (!getOrCreateDirectory(dir).exists()) {
            throw new IllegalStateException("Failed to initiate artifact directory " + dir);
        }
    }

    /**
     * Returns the number of couplings found for the application set.
     *
     * @param appSet             The application set.
     * @param filterConfig       The filters.
     * @param classLoaderService Class loader service.
     * @param reportFile         File name for the report.
     * @return Coupling count for the application set.
     */
    private int analyzeApplicationSet(final ApplicationSet appSet,
                                      final CouplingFilterConfig filterConfig,
                                      final ClassLoaderService classLoaderService,
                                      final String reportFile) {
        log.info("Analyzing applicationSet");
        int appSetCouplingCount = 0;

        final CouplingRecordWriter writer = new CouplingRecordWriter(reportFile);
        for (Application application : appSet.getApplications()) {
            appSetCouplingCount += analyzeApplication(appSet, application, filterConfig, classLoaderService, writer);
        }

        log.info("ApplicationSet={}, TotalClassesAnalyzed={}, TotalCouplingsFound={}",
                 appSet.getAppSetName(), applicationSetClassCount.get(), appSetCouplingCount);

        writer.close();
        log.info("Couplings were saved to {}", reportFile);

        return appSetCouplingCount;
    }

    /**
     * Returns the number of couplings found for the application.
     *
     * @param app                The application.
     * @param filterConfig       The filters.
     * @param classLoaderService Class loader service.
     * @param writer             Coupling record writer.
     * @return Coupling count for the application.
     */
    private int analyzeApplication(final ApplicationSet appSet,
                                   final Application app,
                                   final CouplingFilterConfig filterConfig,
                                   final ClassLoaderService classLoaderService,
                                   final CouplingRecordWriter writer) {
        log.info("Analyzing application: {}", app.getAppName());
        applicationClassCount.set(0);
        int appCouplingCount = 0;

        for (Artifact artifact : app.getArtifacts()) {
            log.info("Analyzing artifact: {}", artifact.toFileName());

            appCouplingCount += analyzeArtifact(appSet, app, artifact, filterConfig, classLoaderService, writer);
        }

        log.info("Application={}, TotalClassesAnalyzed={}, TotalCouplingsFound={}",
                 app.getAppName(), applicationClassCount.get(), appCouplingCount);

        return appCouplingCount;
    }

    /**
     * Returns the number of couplings found for the artifact.
     *
     * @param app                The application.
     * @param artifact           The artifact.
     * @param filterConfig       The filters.
     * @param classLoaderService Class loader service.
     * @param writer             Coupling record writer.
     * @return Coupling count for the artifact.
     */
    private int analyzeArtifact(final ApplicationSet appSet,
                                final Application app,
                                final Artifact artifact,
                                final CouplingFilterConfig filterConfig,
                                final ClassLoaderService classLoaderService,
                                final CouplingRecordWriter writer) {

        final List<ShadowClass> classesFromJarClassLoader = classLoaderService.getAllClasses(artifact);

        applicationSetClassCount.addAndGet(classesFromJarClassLoader.size());
        applicationClassCount.addAndGet(classesFromJarClassLoader.size());

        final UsageCollector usageCollector = new UsageCollector(filterConfig);
        for (ShadowClass c : classesFromJarClassLoader) {
            new FilteredClassVisitor(c.getClassName(), usageCollector, c.getClassBytes()).visit();
        }

        final List<MethodCoupling> couplings = usageCollector.getMethodCouplings();
        log.info("ClassCount={}, CouplingCount={}", classesFromJarClassLoader.size(), couplings.size());

        // Write the CouplingRecord as Json
        couplings.stream()
                 .map(c -> toCouplingRecord(appSet, app, artifact, c))
                 .forEach(writer::writeAsJson);

        return couplings.size();
    }

    private static CouplingRecord toCouplingRecord(final ApplicationSet appSet,
                                                   final Application app,
                                                   final Artifact artifact,
                                                   final MethodCoupling methodCoupling) {
        return new CouplingRecord.Builder()
                   .appSetName(appSet.getAppSetName().orElse(""))
                   .applicationName(app.getAppName())
                   .artifactFileName(artifact.toFileName())
                   .artifactId(artifact.getArtifactId())
                   .artifactGroup(artifact.getGroupId())
                   .artifactVersion(artifact.getVersion())
                   .sourceClass(methodCoupling.getSource().getClassName())
                   .sourceMethod(methodCoupling.getSource().getMethodName())
                   .targetClass(methodCoupling.getTarget().getClassName())
                   .targetMethod(methodCoupling.getTarget().getMethodName())
                   .build();
    }

    private static String applicationSetToString(final ApplicationSet appSet) {
        final StringBuilder buf = new StringBuilder();
        appSet.getApplications()
              .forEach(app -> {
                  buf.append(app.getAppName()).append(':').append('\n');
                  app.getArtifacts().forEach(atf -> {
                      buf.append("  ").append(atf.getArtifactId());
                      buf.append(' ').append(atf.getBaseVersion().orElseGet(atf::getVersion));
                      buf.append(' ').append(atf.getPackaging());
                      buf.append('\n');
                  });
              });

        return buf.toString();
    }
}
