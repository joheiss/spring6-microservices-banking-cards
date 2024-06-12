package com.jovisco.services.cards.dtos;

import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@ConfigurationProperties(prefix = "contactinfo")
public class ContactInfoDto {

    private String message;
    private Map<String, String> contact;
    private List<String> support;
}
