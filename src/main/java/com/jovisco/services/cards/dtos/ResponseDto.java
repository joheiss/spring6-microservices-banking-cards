package com.jovisco.services.cards.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Schema(name = "API Response", description = "Schema to hold API response")
@Data @AllArgsConstructor @Builder
public class ResponseDto {

  @Schema(description = "HTTP status code", example = "200")
  private String statusCode;

  @Schema(description = "Status message", example = "Request processed successfully")
  private String statusMessage;
}
