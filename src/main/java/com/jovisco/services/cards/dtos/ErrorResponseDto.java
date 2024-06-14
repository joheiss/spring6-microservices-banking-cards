package com.jovisco.services.cards.dtos;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Schema(name = "Error Response", description = "Schema to hold error response")
@Data @AllArgsConstructor @Builder
public class ErrorResponseDto {

  @Schema(description = "API path", example = "uri=/api/v1/cards/+122234567890")
  private String apiPath;

  @Schema(description = "Http error code", example = "400")
  private HttpStatus errorCode;

  @Schema(description = "Error message", example = "Card already registered with given mobile number +122234567890")
  private String errorMessage;

  @Schema(description = "Time when error occurred", example = "2024-07-04T10:11:12")
  private LocalDateTime errorTime;
}
