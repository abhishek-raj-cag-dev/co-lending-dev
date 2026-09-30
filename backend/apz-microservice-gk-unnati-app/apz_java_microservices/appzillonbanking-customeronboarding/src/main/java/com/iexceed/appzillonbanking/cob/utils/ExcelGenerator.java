package com.iexceed.appzillonbanking.cob.utils;

import com.iexceed.appzillonbanking.cob.utils.customAnnotations.ExcelColumn;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public final class ExcelGenerator {

    private ExcelGenerator() {}

    public static <T> void generateExcel(List<T> data, OutputStream out, Class<T> type) {
        List<Field> annotatedFields = new ArrayList<>();
        List<String> headers = new ArrayList<>();
        List<Function<T, Object>> extractors = new ArrayList<>();

        // Scan for annotated fields
        for (Field field : type.getDeclaredFields()) {
            ExcelColumn annotation = field.getAnnotation(ExcelColumn.class);
            if (annotation != null) {
                field.setAccessible(true); // safe, simple, no side effects

                annotatedFields.add(field);
                headers.add(annotation.value());

                extractors.add(item -> {
                    try {
                        return field.get(item);
                    } catch (IllegalAccessException e) {
                        return null;
                    }
                });
            }
        }

        writeExcel(data, headers, extractors, out);
    }

    private static <T> void writeExcel(
            List<T> data,
            List<String> headers,
            List<Function<T, Object>> extractors,
            OutputStream out
    ) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Report");

        int rowIndex = 0;

        // Header row
        Row headerRow = sheet.createRow(rowIndex++);
        for (int i = 0; i < headers.size(); i++) {
            headerRow.createCell(i).setCellValue(headers.get(i));
        }

        // Data rows
        for (T item : data) {
            Row row = sheet.createRow(rowIndex++);
            for (int i = 0; i < extractors.size(); i++) {
                Object value = extractors.get(i).apply(item);
                row.createCell(i).setCellValue(value != null ? value.toString() : "");
            }
        }

        // Auto-size columns
        for (int i = 0; i < headers.size(); i++) {
            sheet.autoSizeColumn(i);
        }

        try {
            workbook.write(out);
            workbook.close();
        } catch (Exception e) {
            throw new RuntimeException("Failed to write Excel", e);
        }
    }
}
