package com.unfamilia.eggbot.infrastructure.reporter;

public interface ReportRepository {

    /**
     * Writes report body to local volume.
     * @param reportBody
     */
    void writeReport(String reportBody);

    /**
     * Reads report from local volume.
     * @param
     */
    String readReport();
}
