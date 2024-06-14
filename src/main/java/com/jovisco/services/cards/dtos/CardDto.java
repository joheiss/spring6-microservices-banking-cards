package com.jovisco.services.cards.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Data;

@Schema(name = "Card", description = "Schema to transfer card data")
@Data @Builder
public class CardDto {

  @Schema(description = "Mobile phone number of customer", example = "+122234567890")
  @NotEmpty(message = "Mobile number must not be empty")
  @Pattern(regexp = "^\\+[1-9]{1}[0-9]{3,14}$", message = "Mobile number must be valid")
  private String mobileNumber;

  @Schema(description = "Card number of the customer", example = "1234567890123456")
  @NotEmpty(message = "Card number must not be empty")
  @Pattern(regexp = "(^$|[0-9]{16})", message = "Loan number must be 16 digits")
  private String cardNumber;

  @Schema(description = "Type of card", example = "Credit Card")
  @NotEmpty(message = "Card type must not be empty")
  private String cardType;

  @Schema(description = "Total limit amount", example = "123456")
  @Positive(message = "Total limit must be greater than zero")
  private int totalLimit;

  @Schema(description = "Total amount used", example = "12345")
  @PositiveOrZero(message = "Amount used must be greater than or equal zero")
  private int amountUsed;

  @Schema(description = "Total amount available", example = "98765")
  @PositiveOrZero(message = "Available amount must be greater than or equal zero")
  private int availableAmount;

}
