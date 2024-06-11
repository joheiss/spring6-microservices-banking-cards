package com.jovisco.services.cards.dtos;

import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "cards")
public record CardsContactInfoDto(
        String message,
        Map<String, String> contact,
        List<String> support) {
}
