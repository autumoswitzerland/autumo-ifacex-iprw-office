/**
 * Copyright 2026 autumo GmbH, Michael Gasche.
 * All Rights Reserved.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * Bug fixes, suggestions and comments should be sent to:
 * code@autumo.ch
 *
 */
package ch.autumo.ifacex.ip.office.excel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;

import ch.autumo.ifacex.writer.Writer;

/**
 * Compatibility test of the Excel writer against the ifaceX API and the Apache POI version
 * declared in the POM.
 *
 * <p>It fails when a dependency upgrade changes what this writer is built on: the ifaceX
 * {@link Writer} contract or the POI streaming workbook API. No network access is required.</p>
 */
class ExcelWriterTest {

    /** The writer must still implement the ifaceX writer contract. */
    @Test
    void implementsIfaceXWriter() throws Exception {
        final Class<?> c = Class.forName("ch.autumo.ifacex.ip.office.excel.ExcelWriter");
        assertInstanceOf(Writer.class, c.getDeclaredConstructor().newInstance());
    }

    /** The POI streaming workbook used by the writer must still be available. */
    @Test
    void poiStreamingWorkbookIsAvailable() throws Exception {
        assertNotNull(Class.forName("org.apache.poi.xssf.streaming.SXSSFWorkbook"));
    }

    /**
     * Writes a workbook the same way the writer does (streaming workbook, string cells) and reads
     * it back - this is the runtime check that the declared POI version still produces valid
     * {@code .xlsx} files.
     */
    @Test
    void streamingWorkbookWritesAndReadsBack() throws Exception {
        final Path dir = Files.createTempDirectory("iprw-office-test");
        final File file = dir.resolve("test.xlsx").toFile();
        final SXSSFWorkbook workbook = new SXSSFWorkbook(100);
        try {
            final Sheet sheet = workbook.createSheet("articles");
            final Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("id");
            header.createCell(1).setCellValue("title");
            final Row row = sheet.createRow(1);
            row.createCell(0).setCellValue("1");
            row.createCell(1).setCellValue("First article");

            final FileOutputStream out = new FileOutputStream(file);
            try {
                workbook.write(out);
                out.flush();
            } finally {
                out.close();
            }
        } finally {
            workbook.dispose();
            workbook.close();
        }

        try (XSSFWorkbook read = new XSSFWorkbook(file)) {
            final Sheet sheet = read.getSheet("articles");
            assertNotNull(sheet, "sheet must survive the round trip");
            assertEquals("id", sheet.getRow(0).getCell(0).getStringCellValue());
            assertEquals("title", sheet.getRow(0).getCell(1).getStringCellValue());
            assertEquals("1", sheet.getRow(1).getCell(0).getStringCellValue());
            assertEquals("First article", sheet.getRow(1).getCell(1).getStringCellValue());
        }
    }
}
