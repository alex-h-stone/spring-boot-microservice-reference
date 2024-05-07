package com.cgi.example.common.local;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
class DynamicApplicationProperties {

    @JsonProperty
    private Port applicationPort;

    @JsonProperty
    private Port managementPort;

    @JsonProperty
    private Port wireMockPort;

    @JsonProperty
    private Port mongoDBPort;

    @JsonProperty
    private Port oAuth2Port;
}
