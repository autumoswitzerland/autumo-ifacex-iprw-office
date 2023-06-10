/**
 * Copyright 2023 autumo GmbH, Michael Gasche.
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

import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.autumo.ifacex.Constants;
import ch.autumo.ifacex.IPC;
import ch.autumo.ifacex.IfaceXException;
import ch.autumo.ifacex.Processor;
import ch.autumo.ifacex.SourceEntity;
import ch.autumo.ifacex.WriterMapping;
import ch.autumo.ifacex.batch.BatchData;
import ch.autumo.ifacex.writer.Writer;


/**
 * Excel (streaming) writer.
 */
public class ExcelWriter implements Writer {

	private final static Logger LOG = LoggerFactory.getLogger(ExcelWriter.class.getName());
	
	private String outputFile = null;
	private String fields[] = null;
	
	private WriterMapping mapping = null;
	
	private SXSSFWorkbook workbook = null;
	private Sheet sheet = null;
	
	private int streamBufferSize = 1000;
	
	
	@Override
	public void initialize(String rwName, IPC config, Processor processor) throws IfaceXException {
		
		streamBufferSize = config.getWriterConfig(rwName).getNumber("_buffer_size", 1000);
	}

	@Override
	public void initializeEntity(String writerName, IPC config, SourceEntity entity) throws IfaceXException {

		this.mapping = config.getWriterConfig(writerName).getMapping(entity);
		
		if (mapping.hasDestinationEntity())
			outputFile = config.getWriterConfig(writerName).getFileWithDestName(mapping.getDestinationEntity(), Constants.XLSX_EXTENSION);
		else
			outputFile = config.getWriterConfig(writerName).getFile(entity, Constants.XLSX_EXTENSION);
		
		if (!mapping.hasFieldMappings())
			fields = entity.getSourceFields();
		else
			fields = mapping.getAllFields();
		
		workbook = new SXSSFWorkbook(streamBufferSize);
		sheet = workbook.createSheet(entity.getEntity());
	}

	@Override
	public void writeHeader(String writerName, IPC config, SourceEntity entity) throws IfaceXException {
		
		try {
			final Row headerRow = sheet.createRow(0);
			final CellStyle headerCellStyle = this.setHeaderCellStyle(workbook);

			Integer index = 0; 
			for (String field : fields) { 
				if (field != null && field.trim().length() != 0) {
					this.createCell(headerRow, index, field, headerCellStyle);
					index++; 
				}
			} 
			
	     } catch (Exception e) {
	    	 throw new IfaceXException("Couldn't write excel workbook header!", e);
	     }	
	}

	@Override
	public void writeBatchData(String writerName, IPC config, BatchData batch, SourceEntity entity)
			throws IfaceXException {
		
		try {
			final CellStyle cellStyle = this.setExcelBodyCellStyle(workbook);
			
			int rowNum = sheet.getLastRowNum() + 1;
			while (batch.hasNext()) {
				
				final Row row = sheet.createRow(rowNum);
				
				String values[] = batch.next();
				if (mapping.hasFieldMappings())
					values = mapping.getTextValues(values);
				
				for (int cellNo = 0; cellNo < values.length; cellNo++)
					this.createCell(row, cellNo, values[cellNo], cellStyle);
				
				rowNum++;
			}
			
		} catch (Exception e) {
			throw new IfaceXException("Couldn't write excel workbook data!", e);
		}
	}

	@Override
	public void close(String rwName) throws IfaceXException {
		
		if (workbook != null) {
			try {
				
				final FileOutputStream out = new FileOutputStream(outputFile);
				workbook.write(out);
				
				out.flush();
				out.close();
				
				if (!workbook.dispose())
					LOG.warn("Couldn't dispose temporary excel files!");
				
			} catch (IOException e) {
				LOG.error("Couldn't write final excel file '"+outputFile+"'!", e);
			}
		}
	}
	
	private void createCell(Row row, Integer index, String value, CellStyle cellStyle) {
		final Cell cell = row.createCell(index.intValue());
		if (cellStyle != null)
			cell.setCellStyle(cellStyle);
		cell.setCellValue(value); // string
	}

	private CellStyle setHeaderCellStyle(SXSSFWorkbook workbook) {
		
		final Font font = workbook.createFont();
	    font.setBold(true);
	    font.setItalic(false);

	    final CellStyle style = workbook.createCellStyle();
	    style.setFont(font);		
		return style;
	}

	private CellStyle setExcelBodyCellStyle(SXSSFWorkbook workbook) {
		return workbook.createCellStyle();
	}
	
}
