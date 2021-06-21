package department.gui.month.data.export;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.List;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;

import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.ss.util.RegionUtil;

import org.apache.poi.xssf.usermodel.DefaultIndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import department.database.tables.Department;
import department.database.tables.teacher.Teacher;

import department.gui.ErrorHandler;

import department.gui.month.table.DayType;
import department.gui.month.table.MonthTable;
import department.gui.month.table.MonthTableItem;
import department.gui.month.table.MonthTableItem.DayOfMonth;

public class ExcelWorkbook {
	
	private static final int ROW_START_INDEX = 14;
	
	private static final XSSFColor WEEKEND_COLOR;
	private static final XSSFColor SEPARATOR_COLOR;
	
	private XSSFCellStyle signBottomCellStyle;
	private XSSFCellStyle signBottomBoldCellStyle;
	
	private XSSFCellStyle defaultCellStyle;
	
	private XSSFWorkbook workbook;
	
	static {
		WEEKEND_COLOR = new XSSFColor(getRGB(204, 192, 218), new DefaultIndexedColorMap());
		SEPARATOR_COLOR = new XSSFColor(getRGB(0, 0, 0), new DefaultIndexedColorMap());
	}
	
	private static byte[] getRGB(int r, int g, int b) {
		byte[] rgb = new byte[3];
		
		rgb[0] = (byte) r;
		rgb[1] = (byte) g;
		rgb[2] = (byte) b;
		
		return rgb;
	}
	
	private List<MonthTable> tables;
	
	public ExcelWorkbook(List<MonthTable> tables) {
		this.tables = tables;
		
		try (InputStream is = ExcelWorkbook.class.getResourceAsStream("/pattern.xlsx")) {
			workbook = new XSSFWorkbook(is);
			
			// defaultCellStyle
			defaultCellStyle = workbook.createCellStyle();
			defaultCellStyle.setBorderBottom(BorderStyle.THIN);
			defaultCellStyle.setBorderTop(BorderStyle.THIN);
			defaultCellStyle.setBorderLeft(BorderStyle.THIN);
			defaultCellStyle.setBorderRight(BorderStyle.THIN);
			
			Font font = workbook.createFont();
			font.setFontName("Times New Roman");
			
			defaultCellStyle.setFont(font);
			defaultCellStyle.setAlignment(HorizontalAlignment.CENTER);
			defaultCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			
			// signBottomCellStyle
			signBottomCellStyle = workbook.createCellStyle();
			
			font = workbook.createFont();
			font.setFontName("Times New Roman");
			font.setFontHeightInPoints((short) 14);
			
			signBottomCellStyle.setFont(font);
			
			// signBottomBoldCellStyle
			signBottomBoldCellStyle = workbook.createCellStyle();
			
			font = workbook.createFont();
			font.setFontName("Times New Roman");
			font.setFontHeightInPoints((short) 14);
			font.setBold(true);
			
			signBottomBoldCellStyle.setFont(font);
		} catch (IOException e) {
			ErrorHandler.show(e);
		}
	}
	
	private XSSFWorkbook getWorkbook() {
		for (MonthTable table : tables) {
			XSSFSheet sheet = workbook.cloneSheet(0, table.getDepartment().getName());
			parseSheet(table, sheet);
		}
		// удаляем шаблонный лист
		workbook.removeSheetAt(0);
		return workbook;
	}
	
	private void parseSheet(MonthTable table, XSSFSheet sheet) {
		Department departmnent = table.getDepartment();
		
		// установка месяця таблицы
		sheet.getRow(6).getCell(28).setCellValue(table.getMonth().toString());
		
		// установка названия отдела
		sheet.getRow(13).getCell(0).setCellValue(departmnent.getName());
		
		int rowIndex = ROW_START_INDEX;
		
		for (MonthTableItem item : table.getTableItems()) {
			Row row = sheet.createRow(rowIndex++);
			
			Teacher teacher = item.getTeacher();
			int cellIndex = 0;
			
			createCell(sheet, row, cellIndex++, teacher.getGender().toString());
			createResizedCell(sheet, row, cellIndex++, teacher.getName(), null);
			
			int dayIndex = 0;
			for (DayOfMonth day : item.getDays()) {
				DayType dayType = day.getMonthDayType();
				
				boolean isWeekend = dayType == DayType.WEEKEND || dayType == DayType.HOLIDAY;
				boolean isSeparator = dayIndex++ == 15;
				
				XSSFColor color = isSeparator ? SEPARATOR_COLOR : (isWeekend ? WEEKEND_COLOR : null);
				createResizedCell(sheet, row, cellIndex++, day.getValue(), color);
			}
			
			createCell(sheet, row, cellIndex++, String.valueOf(item.getAllDays())); // днів
			createCell(sheet, row, cellIndex++, String.valueOf(item.getAllHours())); // всього годин
			createCell(sheet, row, cellIndex++, ""); // нічних
		}
		
		// Заступник директора з ... | ім'я
		createBottomSign(sheet, sheet.createRow(rowIndex++), "Заступник директора з " + 
				departmnent.getDeputyDirectorWith(), departmnent.getDeputyDirector());
		
		createBottomSign(sheet, sheet.createRow(rowIndex++), "Директор коледжу", "Олександр ПІТЯКОВ");
	}
	
	private void createBottomSign(Sheet sheet, Row row, String leftSign, String rightSign) {
		row.setHeightInPoints(30);
		
		// left sign
		Cell cell = row.createCell(0);
		cell.setCellValue(leftSign);
		cell.setCellStyle(signBottomCellStyle);
		
		CellUtil.setVerticalAlignment(cell, VerticalAlignment.CENTER);
		mergedAndSetBorder(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 10), sheet);
		
		// right sign
		cell = row.createCell(11);
		cell.setCellValue(rightSign);
		cell.setCellStyle(signBottomBoldCellStyle);
		
		CellUtil.setVerticalAlignment(cell, VerticalAlignment.CENTER);
		CellUtil.setAlignment(cell, HorizontalAlignment.RIGHT);
		mergedAndSetBorder(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 11, 36), sheet);
	}
	
	private void createCell(Sheet sheet, Row row, int cellIndex, String cellValue) {
		Cell cell = row.createCell(cellIndex);
		cell.setCellValue(cellValue);
		cell.setCellStyle(defaultCellStyle);
	}
	
	private int maxNumCharacters(Sheet sheet, int cellIndex) {
		// подсчёт максимального кол-ва символов для ресайза. Минимальное кол-во – 4
		int maxChars = 4;
		for (int i = sheet.getFirstRowNum(); i < sheet.getLastRowNum(); i++) {
			if (i != 8 && i != 9) {
				Row row = sheet.getRow(i);
				Cell cell = row.getCell(cellIndex);
				int size = cell.toString().length();
				if (maxChars < size)
					maxChars = size;
			}
		}
		return maxChars;
	}
	
	private void createResizedCell(Sheet sheet, Row row, int cellIndex, String cellValue, XSSFColor color) {
		Cell cell = row.createCell(cellIndex);
		cell.setCellValue(cellValue);
		
		if (!cellValue.isEmpty()) {
			// sheet.autoSizeColumn(cellIndex, true);
			int width = (int)(maxNumCharacters(sheet, cellIndex) * 1.14388) * 256;
			sheet.setColumnWidth(cellIndex, width);
		}
		
		XSSFCellStyle style = workbook.createCellStyle();
		style.cloneStyleFrom(defaultCellStyle);
		
		if (color != null) {
			style.setFillForegroundColor(color);
			style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		}
		
		cell.setCellStyle(style);
	}
	
	private void mergedAndSetBorder(CellRangeAddress region, Sheet sheet) {
		sheet.addMergedRegion(region);
		
		RegionUtil.setBorderBottom(BorderStyle.THIN, region, sheet);
        RegionUtil.setBorderLeft(BorderStyle.THIN, region, sheet);
        RegionUtil.setBorderRight(BorderStyle.THIN, region, sheet);
        RegionUtil.setBorderTop(BorderStyle.THIN, region, sheet);
	}
	
	public void save(File file) {
		new Thread(() -> {
			try (FileOutputStream os = new FileOutputStream(file)) {
				getWorkbook().write(os);
			} catch (IOException e) {
				ErrorHandler.show(e);
			}
		}).start();	
	}
}