package com.cgi.example.common.local.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Port {

    @JsonProperty
    private Integer port;

    @JsonProperty
    private Instant modifiedAt;

    @JsonProperty
    private ApplicationModule modifiedBy;
}
