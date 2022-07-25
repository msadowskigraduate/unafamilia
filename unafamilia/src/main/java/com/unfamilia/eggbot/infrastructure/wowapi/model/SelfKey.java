package com.unfamilia.eggbot.infrastructure.wowapi.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SelfKey {
    private Self self;

    public static class Self {
        private String href;
    }
}
