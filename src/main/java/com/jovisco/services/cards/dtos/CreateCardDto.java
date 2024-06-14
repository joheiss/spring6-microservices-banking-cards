package com.jovisco.services.cards.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "Create Card", description = "Schema to create a payment card")
@NoArgsConstructor @AllArgsConstructor @Data
public class CreateCardDto {

  @Schema(description = "Mobile phone number of customer", example = "+122234567890")
  @NotEmpty(message = "Mobile number must not be empty")
  @Pattern(regexp = "^\\+[1-9]{1}[0-9]{3,14}$", message = "Mobile number must be valid")
  private String mobileNumber;
}
