package com.unfamilia.application.audit.query;

import java.util.List;

import com.unfamilia.application.audit.RosterAuditModel;
import com.unfamilia.application.command.Command;
import com.unfamilia.application.query.Query;
import com.unfamilia.eggbot.infrastructure.wowguild.model.CharacterProfileResponse;

import lombok.AllArgsConstructor;

@AllArgsConstructor(staticName = "of")
public class GenerateNewAuditQuery implements Query<List<RosterAuditModel>> {
    
}
