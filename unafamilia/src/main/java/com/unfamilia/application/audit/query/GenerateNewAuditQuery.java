package com.unfamilia.application.audit.query;

import java.util.List;

import com.unfamilia.application.audit.RosterAuditModel;
import com.unfamilia.application.query.Query;
import lombok.AllArgsConstructor;

@AllArgsConstructor(staticName = "of")
public class GenerateNewAuditQuery implements Query<List<RosterAuditModel>> {
    
}
