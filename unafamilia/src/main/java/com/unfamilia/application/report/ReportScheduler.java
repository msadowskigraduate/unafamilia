package com.unfamilia.application.report;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.unfamilia.application.query.QueryBus;
import com.unfamilia.application.report.query.NewWishlistReportQuery;
import com.unfamilia.eggbot.infrastructure.reporter.ReportRepository;

import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import io.quarkus.scheduler.Scheduled;

@ApplicationScoped
public class ReportScheduler {
    @Inject QueryBus bus;
    @Inject ReportRepository reportRepository;

    @Inject 
    @Location("partials/reports")
    Template reportPartials;
    
    @Scheduled(cron="0 0 19 ? * MON,WED *") 
    void generate() {
        List<String> difficulty = Stream.of("heroic","mythic").toList();
        Set<String> roles = Stream.of("Melee", "Ranged").collect(Collectors.toSet());
        String result = reportPartials
            .data("timestamp", ZonedDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME))
            .data("reports", bus.handle(new NewWishlistReportQuery(difficulty, roles)))
            .render();
        
        reportRepository.writeReport(result);
    }
}
