package com.unfamilia.eggbot.infrastructure.wowapi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class WowItem {
    private Long id;
    private String name;

    @JsonProperty("item_subclass")
    private WowItemClass itemClass;

    private String mediaLink;

    @JsonProperty("media")
    public void setMediaLink(Map<String, Object> media) {
        var values = media.get("key");

        if(values instanceof Map) {
            this.mediaLink = (String) ((Map<?, ?>) values).get("href");
        }
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class WowItemClass {
        private String name;
        private Long id;
    }
}
