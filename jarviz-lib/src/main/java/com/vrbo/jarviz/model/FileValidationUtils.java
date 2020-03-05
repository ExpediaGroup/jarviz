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

public final class FileValidationUtils {

    private FileValidationUtils() {}

    /**
     * Verifies that the given segment of the filename is valid and
     * not containing the file separator (system dependent).
     */
    public static boolean validFileNamePart(final String part) {
        return part == null || part.isEmpty() || !part.contains(File.separator);
    }
}
