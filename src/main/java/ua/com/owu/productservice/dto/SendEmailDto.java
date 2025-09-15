package ua.com.owu.productservice.dto;

import lombok.Builder;

import java.util.Map;

@Builder
public record SendEmailDto(
        String to,
        String subject,
        String templateName,
        Map<String, Object> contextData,
        String body
) {
}
