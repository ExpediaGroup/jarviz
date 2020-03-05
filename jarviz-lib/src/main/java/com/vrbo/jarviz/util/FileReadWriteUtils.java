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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.file.Files;
import java.util.function.Consumer;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.io.ByteSource;

public final class FileReadWriteUtils {

    private static final Logger log = LoggerFactory.getLogger(FileReadWriteUtils.class);

    private FileReadWriteUtils() {}

    /**
     * This will return the directory if it exists.
     * Otherwise this will create a new directory and returns it.
     */
    public static File getOrCreateDirectory(@Nonnull final String path) {
        final File file = new File(path);

        if (!file.exists()) {
            if (!file.mkdirs()) {
                log.error("Unable to create directory {}", path);
                throw new IllegalArgumentException(String.format("Unable to create directory %s", path));
            }
        }

        if (!file.isDirectory()) {
            log.error("File should be a directory: {}", path);
            throw new IllegalArgumentException(String.format("File should be a directory: %s", path));
        }

        return file;
    }

    /**
     * This will read a resource file from the classpath and return the content as a string.
     */
    public static String readResourceAsString(@Nonnull final String resourceFileName) {
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream(resourceFileName)) {
            final ByteSource byteSource = new ByteSource() {

                @Override
                public InputStream openStream() {
                    return inputStream;
                }
            };

            return byteSource.asCharSource(Charsets.UTF_8).read();
        } catch (IOException e) {
            throw new IllegalArgumentException(String.format("Unable to load resource %s", resourceFileName), e);
        }
    }

    /**
     * This will read the content from a file and return as a string.
     */
    public static String readFileAsString(@Nonnull final File file) {
        try {
            return new String(Files.readAllBytes(file.toPath()), Charsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalArgumentException(String.format("Unable to read file %s", file.getPath()), e);
        }
    }

    /**
     * This will return the full path for the given file name.
     */
    public static String toFullPath(@Nonnull final String dirPath, @Nonnull final String fileName) {
        final String dirPathWithSeparator = dirPath.endsWith(File.separator) ? dirPath : dirPath + File.separator;
        final String fullFileName = dirPathWithSeparator + fileName;

        if (!getCanonicalName(fullFileName).startsWith(getCanonicalName(dirPathWithSeparator))) {
            throw new IllegalArgumentException("Invalid file name: " + fileName);
        }

        return fullFileName;
    }

    private static String getCanonicalName(final String file) {
        try {
            return new File(file).getCanonicalPath();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Writes to a UTF-8 encoded file
     */
    public static void writeToFile(final String fileName, final Consumer<Writer> writeHandler) {
        try {
            final Writer writer = new PrintWriter(fileName, Charsets.UTF_8.name());
            writeHandler.accept(writer);
            writer.close();
        } catch (IOException e) {
            log.error("Cannot write to file {}", fileName);
            throw new IllegalArgumentException(String.format("Cannot write to file %s", fileName), e);
        }
    }

}
