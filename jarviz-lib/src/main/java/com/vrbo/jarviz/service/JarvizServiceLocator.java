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

import javax.annotation.Nonnull;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.ServiceLocatorFactory;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import com.vrbo.jarviz.config.JarvizConfig;

public class JarvizServiceLocator {

    public static ServiceLocator createServiceLocator(@Nonnull final JarvizConfig jarvizConfig) {
        return createServiceLocator(jarvizConfig, JarvizServiceLocator.class.getName());
    }

    public static ServiceLocator createServiceLocator(@Nonnull final JarvizConfig jarvizConfig, @Nonnull final String name) {
        final ServiceLocator serviceLocator = ServiceLocatorFactory.getInstance().create(name);
        ServiceLocatorUtilities.bind(serviceLocator, new AbstractBinder() {

            @Override
            protected void configure() {
                // configs
                bind(jarvizConfig).to(JarvizConfig.class);
                bind(MavenArtifactDiscoveryService.class).to(ArtifactDiscoveryService.class);

                bind(JarClassLoaderService.class).to(ClassLoaderService.class);
            }
        });

        return serviceLocator;
    }
}
