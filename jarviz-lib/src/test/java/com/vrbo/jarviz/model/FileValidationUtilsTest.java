package com.vrbo.jarviz.model;

import java.io.File;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

import static com.vrbo.jarviz.model.FileValidationUtils.validFileNamePart;

public class FileValidationUtilsTest {


    @Test
    public void testValidFileNamePart() {
        assertThat(validFileNamePart("")).isTrue();
        assertThat(validFileNamePart("Hello World!")).isTrue();

        assertThat(validFileNamePart(File.separator)).isFalse();
        assertThat(validFileNamePart("/foo\\")).isFalse();
    }
}
