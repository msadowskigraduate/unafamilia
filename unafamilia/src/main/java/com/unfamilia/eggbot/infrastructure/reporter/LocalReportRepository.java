package com.unfamilia.eggbot.infrastructure.reporter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class LocalReportRepository implements ReportRepository {
    private static String REPORT_FILE_NAME = "report";
    private static String REPORT_FILE_EXTENSION = ".tmp";

    @Override
    public void writeReport(String reportBody) {
        try {
            Path path = Paths.get(REPORT_FILE_NAME + REPORT_FILE_EXTENSION);
            byte[] reportBodyToBytes = reportBody.trim().getBytes();
            Files.write(path, reportBodyToBytes);
        } catch (IOException exception) {
            throw new RuntimeException(exception.getMessage());
        }
    }

    @Override
    public String readReport() {
        try {
            Path path = Paths.get(REPORT_FILE_NAME + REPORT_FILE_EXTENSION);
            if(!Files.exists(path)) {
                return null;
            }
            return Files.readAllLines(path).stream().reduce("", String::concat);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}