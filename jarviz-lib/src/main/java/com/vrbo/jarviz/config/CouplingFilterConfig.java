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

import java.util.Optional;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.vrbo.jarviz.model.CouplingFilter;

/**
 * Filter configuration to select appropriate couplings in the final coupling matrix.
 * Sample JSON representation:
 * <pre>
 *   {
 *     "include": {
 *       "targetPackage": "^(com\\.xyz\\.foo|com\\.xyz\\.bar).*$"
 *     },
 *     "exclude": {
 *       "sourcePackage": "^(com\\.xyz\\.foo|com\\.xyz\\.bar).*$"
 *     }
 *   }
 * </pre>
 */
@Value.Immutable
@JsonSerialize(as = ImmutableCouplingFilterConfig.class)
@JsonDeserialize(as = ImmutableCouplingFilterConfig.class)
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public interface CouplingFilterConfig {

    /**
     * An optional {@link CouplingFilter} that will keep all the couplings that are matching.
     * If this is empty, every coupling will be considered a match, therefore selecting all the couplings.
     * Note: This filter rule will be applied in conjunction (AND operation) with {@link #getExclude()} field.
     *
     * @return The {@link CouplingFilter} for inclusions.
     */
    Optional<CouplingFilter> getInclude();

    /**
     * An optional {@link CouplingFilter} that will discard all the couplings that are matching.
     * If this is empty, every coupling will be considered a non-match, therefore selecting all the couplings.
     * Note: This filter rule will be applied in conjunction (AND operation) with {@link #getInclude()} field.
     *
     * @return The {@link CouplingFilter} for exclusions.
     */
    Optional<CouplingFilter> getExclude();

    class Builder extends ImmutableCouplingFilterConfig.Builder {}
}
