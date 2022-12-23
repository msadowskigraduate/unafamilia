package com.unfamilia.application.report.query;

import java.util.List;
import java.util.Set;

import com.unfamilia.application.query.Query;
import com.unfamilia.eggbot.infrastructure.reporter.WishlistReport;

public record NewWishlistReportQuery(List<String> difficulty, Set<String> roles) implements Query<List<WishlistReport>> {
    
}
