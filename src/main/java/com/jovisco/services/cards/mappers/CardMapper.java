package com.jovisco.services.cards.mappers;

import com.jovisco.services.cards.dtos.CardDto;
import com.jovisco.services.cards.entities.Card;

public class CardMapper {

  public static CardDto mapToCardDto(Card card) {

    return CardDto.builder()
        .mobileNumber(card.getMobileNumber())
        .cardNumber(card.getCardNumber())
        .cardType(card.getCardType())
        .totalLimit(card.getTotalLimit())
        .amountUsed((card.getAmountUsed()))
        .availableAmount(card.getAvailableAmount())
        .build();
  }

  public static Card mapToCard(CardDto cardDto) {

    return Card.builder()
        .mobileNumber(cardDto.getMobileNumber())
        .cardNumber(cardDto.getCardNumber())
        .cardType(cardDto.getCardType())
        .totalLimit(cardDto.getTotalLimit())
        .amountUsed((cardDto.getAmountUsed()))
        .availableAmount(cardDto.getAvailableAmount())
        .build();
  }
}
