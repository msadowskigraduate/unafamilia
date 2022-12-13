package com.unfamilia.application.report.query;

import java.util.List;

import com.unfamilia.application.query.Query;
import com.unfamilia.eggbot.infrastructure.reporter.WishlistReport;

public record NewWishlistReportQuery(List<String> excludedDifficulties) implements Query<List<WishlistReport>> {
    
}
