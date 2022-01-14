package com.unfamilia.eggbot.domain.wowtoken;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

import static lombok.AccessLevel.PROTECTED;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class WoWToken {
     public final static String STATIC_IMAGE = "";
     private Date lastUpdatedTimestamp;
     private Integer price;

     public void setLastUpdatedTimestamp(Long lastUpdatedTimestamp) {
          this.lastUpdatedTimestamp = new Date(lastUpdatedTimestamp);
     }

     public void setPrice(Double price) {
          this.price = price.intValue() / 10000;
     }
}
