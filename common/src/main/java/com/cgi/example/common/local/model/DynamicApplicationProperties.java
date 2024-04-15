package com.cgi.example.common.local.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DynamicApplicationProperties {

    @JsonProperty
    private Port applicationPort;

    @JsonProperty
    private Port managementPort;

    @JsonProperty
    private Port wireMockPort;

    @JsonProperty
    private Port mongoDBPort;
}
