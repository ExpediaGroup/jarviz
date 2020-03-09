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

public final class NamingUtils {

    private NamingUtils() {}

    /**
     * Converts a class name from bytecode format to source code format:
     * com/foo/bar/MyClass to com.foo.bar.MyClass
     *
     * @param className The class name.
     * @return Formatted class name.
     */
    public static String toSourceCodeFormat(final String className) {
        if (className == null) {
            return null;
        }

        return className.replaceAll("/", ".");
    }

    /**
     * Converts a class name from bytecode format to source code format:
     * com.foo.bar.MyClass to com/foo/bar/MyClass
     *
     * @param className The class name.
     * @return Formatted class name.
     */
    public static String toBytecodeFormat(final String className) {
        if (className == null) {
            return null;
        }

        return className.replaceAll("\\.", "/");
    }
}
