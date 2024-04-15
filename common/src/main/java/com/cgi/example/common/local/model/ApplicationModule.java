package com.cgi.example.common.local.model;

import lombok.Getter;

@Getter
public enum ApplicationModule {

    API_TEST("The api-test module"),
    COMMON_TEST("The test part of the common module i.e. common/src/test"),
    LOAD_TEST("The load-test module"),
    APPLICATION_MAIN("The main/root/parent application i.e. src/main"),
    APPLICATION_TEST("The main/root/parent application tests i.e. src/test");

    private final String description;

    ApplicationModule(String description) {
        this.description = description;
    }
}
