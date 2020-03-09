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

package com.vrbo.jarviz.util;

import java.io.IOException;
import java.util.Collection;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public final class JsonUtils {

    public static final ObjectMapper OBJECT_MAPPER = createObjectMapper();

    private JsonUtils() {}

    private static ObjectMapper createObjectMapper() {
        final ObjectMapper om =
            new ObjectMapper()
                .registerModule(new GuavaModule())
                .registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule());

        // Make sure time is written as a formatted string
        om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return om;
    }

    /**
     * Serializes the given object into a JSON blob.
     *
     * @param object Object to be serialized.
     * @return JSON representation of the object.
     */
    public static String toJsonString(final Object object) {
        try {
            Object effectiveObject = object;
            if (object instanceof Collection) {
                effectiveObject = new JsonCollectionSerializationWrapper((Collection) object);
            }

            return OBJECT_MAPPER.writeValueAsString(effectiveObject);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Deserializes the given object into a JSON blob.
     *
     * @param json  JSON representation of the object.
     * @param clazz Type of the object to be created.
     * @param <T>   Deserialized object.
     * @return The deserialized object.
     */
    public static <T> T fromJsonString(final String json, final Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(json, clazz);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static class JsonCollectionSerializationWrapper {

        private final Collection<?> results;

        public JsonCollectionSerializationWrapper(final Collection<?> results) {
            this.results = results;
        }

        public Collection<?> getResults() {
            return results;
        }
    }
}

