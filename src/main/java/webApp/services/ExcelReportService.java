package webApp.services;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import webApp.models.Laptop;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;

@Service
public class ExcelReportService {



    public byte[] generateExcelReport(List<Laptop> favoriteLaptops) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Favorite Laptops");

        // ✅ Create Header Row
        Row headerRow = sheet.createRow(0);
        String[] columns = {"Lot Number", "Laptop Model", "Description", "Boncoin Price"};

        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(getHeaderStyle(workbook)); // Apply header styling
        }

        // ✅ Sort laptops by Lot Number before writing to Excel
        favoriteLaptops.sort(Comparator.comparingInt(Laptop::getLotNumber));

        // ✅ Fill Data
        int rowNum = 1;
        for (Laptop laptop : favoriteLaptops) {
            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(laptop.getLotNumber()); // Lot Number
            row.createCell(1).setCellValue(laptop.getModel()); // Model
            row.createCell(2).setCellValue(laptop.getSimpleSpecs()); // Only specs description
            row.createCell(3).setCellValue(laptop.getBonCoinEstimation()); // Boncoin Price
        }

        // ✅ Auto-size columns for better readability
        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // ✅ Write to ByteArrayOutputStream
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        return outputStream.toByteArray();
    }

    // ✅ Style for header row
    private CellStyle getHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }
}
