package br.com.daniel.file.exporter.factory;

import br.com.daniel.exception.BadRequestException;
import br.com.daniel.file.exporter.contract.FileExporter;
import br.com.daniel.file.exporter.impl.CsvExporter;
import br.com.daniel.file.exporter.impl.XlsxExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import static br.com.daniel.file.exporter.MediaTypes.APPLICATION_CSV_VALUE;
import static br.com.daniel.file.exporter.MediaTypes.APPLICATION_XLSX_VALUE;

@Component
public class FileExporterFactory {

    private Logger logger = LoggerFactory.getLogger(FileExporterFactory.class);

    @Autowired
    private ApplicationContext context;

    public FileExporter getExporter(String acceptHeader) throws Exception {
        if(acceptHeader.equalsIgnoreCase(APPLICATION_XLSX_VALUE)) {
            return context.getBean(XlsxExporter.class);
        } else if(acceptHeader.equalsIgnoreCase(APPLICATION_CSV_VALUE)) {
            return context.getBean(CsvExporter.class);
        } else {
            throw new BadRequestException("Invalid File Format!");
        }

    }

}
