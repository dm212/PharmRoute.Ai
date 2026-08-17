package com.pharmatrace.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("pharmatrace.database")
public record DatabaseProperties(String uri, String username, String password) {
}
