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

	private String outputFile = null;
	private String fields[] = null;
	
	private WriterMapping mapping = null;
	
	private SXSSFWorkbook workbook = null;
	
	
	@Override
	public void initialize(String rwName, IPC config, Processor processor) throws IfaceXException {
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
	}

	@Override
	public void writeHeader(String writerName, IPC config, SourceEntity entity) throws IfaceXException {
		
		workbook = new SXSSFWorkbook(1);
		
		try {
			
			final Sheet sheet = workbook.createSheet(entity.getEntity());            
			final Row headerRow = sheet.createRow(0);       

			Integer index = 0; 
			for (String field : fields) { 
				if (field != null && field.trim().length() != 0) {
					this.createCell(headerRow, index, field, this.setHeaderCellStyle(workbook));
					index++; 
				}
			} 
		     
			final FileOutputStream out = new FileOutputStream(outputFile, true);
			workbook.write(out);
			
	     } catch (Exception e) {
	    	 throw new IfaceXException("Couldn't write excel workbook header!", e);
	     } finally {
	    	 if (workbook != null)
	    		 workbook.dispose();
	     }		
	}

	@Override
	public void writeBatchData(String writerName, IPC config, BatchData batch, SourceEntity entity)
			throws IfaceXException {
		
		workbook = new SXSSFWorkbook(1);
		
		try {
			
			final Sheet sheet = workbook.createSheet(entity.getEntity());
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
			
			final FileOutputStream out = new FileOutputStream(outputFile, true);
			workbook.write(out);
			
		} catch (Exception e) {
			throw new IfaceXException("Couldn't write excel workbook data!", e);
		} finally {
			if (workbook != null)
				workbook.dispose();
		}
	}

	@Override
	public void close(String rwName) throws IfaceXException {
		if (workbook != null)
			try {
				workbook.close();
			} catch (IOException e1) {
			}	    	 
	}
	
	private void createCell(Row row, Integer index, String field, CellStyle cellStyle) {
		final Cell cell = row.createCell(index.intValue());
		cell.setCellStyle(cellStyle);
		cell.setCellValue(field); // string
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
