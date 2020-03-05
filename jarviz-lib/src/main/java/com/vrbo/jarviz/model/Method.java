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

import java.util.Comparator;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Represents a fully qualified name of a method
 */
@Value.Immutable
@JsonSerialize(as = ImmutableMethod.class)
@JsonDeserialize(as = ImmutableMethod.class)
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public interface Method {

    Comparator<Method> COMPARATOR = (a, b) -> {
        final int comparison = a.getClassName().compareTo(b.getClassName());
        if (comparison == 0) {
            return a.getMethodName().compareTo(b.getMethodName());
        }
        return comparison;
    };

    /**
     * Fully qualified class name
     * e.g.: foo.bar.MyClass
     */
    String getClassName();

    /**
     * Method name in the class
     * e.g.: doThat
     */
    String getMethodName();

    /**
     * e.g.: foo.bar.MyClass#doThat
     */
    @JsonIgnore
    default String toStringShort() {
        return getClassName() + "#" + getMethodName();
    }

    /**
     * Simple class name
     * e.g.: For "foo.bar.MyClass", returns "MyClass"
     */
    default String getSimpleClassName() {
        final String fullName = getClassName();
        final int index = fullName.lastIndexOf('.');
        return index > 0 ? fullName.substring(index + 1) : fullName;
    }

    /**
     * Package name of the class
     * e.g.: For "foo.bar.MyClass", returns "foo.bar"
     */
    default String getPackageName() {
        final String fullName = getClassName();
        final int index = fullName.lastIndexOf('.');
        return index > 0 ? fullName.substring(0, index) : "";
    }

    class Builder extends ImmutableMethod.Builder {}

}
