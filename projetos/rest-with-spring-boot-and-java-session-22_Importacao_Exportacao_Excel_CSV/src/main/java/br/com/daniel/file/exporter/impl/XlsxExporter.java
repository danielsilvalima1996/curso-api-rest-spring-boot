package br.com.daniel.file.exporter.impl;

import br.com.daniel.data.dto.PersonDTO;
import br.com.daniel.file.exporter.contract.FileExporter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Component
public class XlsxExporter implements FileExporter {

    @Override
    public Resource exportFile(List<PersonDTO> people) throws Exception {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("People");

            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "First Name", "Last Name", "Address", "Gender", "Enabled"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(createCellStyle(workbook));
            }

            int rowNum = 1;
            for (PersonDTO person : people) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(person.getId());
                row.createCell(1).setCellValue(person.getFirstName());
                row.createCell(2).setCellValue(person.getLastName());
                row.createCell(3).setCellValue(person.getAddress());
                row.createCell(4).setCellValue(person.getGender());
                row.createCell(5).setCellValue(
                        person.getGender() != null && person.getEnabled() ? "YES" : "NO");
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            workbook.close();
            return new ByteArrayResource(outputStream.toByteArray());
        }

    }

    private CellStyle createCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }
}
