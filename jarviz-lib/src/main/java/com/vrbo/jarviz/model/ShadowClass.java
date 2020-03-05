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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * This class holds the meta information about a Java class itself
 * along with the actual bytes of the class file (contains bytecode).
 * The advantage is we can create a ShadowClass (which represents
 * a particular class) for analysis, without loading that particular class
 * into the memory.
 */
@Value.Immutable
@JsonSerialize(as = ImmutableShadowClass.class)
@JsonDeserialize(as = ImmutableShadowClass.class)
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public interface ShadowClass {

    /**
     * Fully qualified class name
     */
    String getClassName();

    /**
     * The actual bytes of the class itself, which can be loaded to the memory
     */
    @JsonIgnore
    @Value.Redacted
    @Value.Default
    default byte[] getClassBytes() {
        return new byte[] {};
    }

    class Builder extends ImmutableShadowClass.Builder {}
}
