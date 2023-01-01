package com.unfamilia.application.audit.query;

import javax.ws.rs.core.Response;

import com.unfamilia.application.query.Query;

public class QueryGuildReportRanking implements Query<Response> {
    public static final String QUERY = "{\"query\":\"{\\n\\treportData {\\n\\t\\treports(guildName: \\\"Una Familia\\\", guildServerSlug: \\\"magtheridon\\\", guildServerRegion: \\\"eu\\\", zoneID: 31, limit: 5) {\\n\\t\\t\\tdata {\\n\\t\\t\\t\\tcode\\n\\t\\t\\t\\ttitle\\n\\t\\t\\t\\tstartTime\\n\\t\\t\\t\\tendTime\\n\\t\\t\\t\\trankings(compare: Parses, playerMetric: dps)\\n\\t\\t\\t}\\n\\t\\t}\\n\\t}\\n}\",\"variables\":null}";
}
