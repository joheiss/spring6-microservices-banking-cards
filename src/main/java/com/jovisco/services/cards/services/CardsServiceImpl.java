package com.jovisco.services.cards.services;

import java.util.Random;

import org.springframework.stereotype.Service;

import com.jovisco.services.cards.constants.CardsConstants;
import com.jovisco.services.cards.dtos.CardDto;
import com.jovisco.services.cards.dtos.CreateCardDto;
import com.jovisco.services.cards.entities.Card;
import com.jovisco.services.cards.exceptions.CardAlreadyExistsException;
import com.jovisco.services.cards.exceptions.ResourceNotFoundException;
import com.jovisco.services.cards.mappers.CardMapper;
import com.jovisco.services.cards.repositories.CardsRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CardsServiceImpl implements CardsService {

  private final CardsRepository cardsRepository;

  @Override
  public void createCard(CreateCardDto createCardDto) {

    // first check if there is already a card for the given mobile number
    cardsRepository
        .findByMobileNumber(createCardDto.getMobileNumber())
        .ifPresent(c -> {
          throw new CardAlreadyExistsException("Card already exists for mobile number: " + createCardDto
              .getMobileNumber());
        });

    // prepare data for new card
    var card = buildNewCard(createCardDto);
    cardsRepository.save(card);

  }

  private Card buildNewCard(CreateCardDto createCardDto) {

    long randomNumber = 1000000000000000L + new Random().nextLong(900000000000000L);

    return Card.builder()
        .mobileNumber(createCardDto.getMobileNumber())
        .cardNumber(Long.toString(randomNumber))
        .cardType(CardsConstants.CREDIT_CARD)
        .totalLimit(CardsConstants.NEW_CARD_LIMIT)
        .amountUsed(0)
        .availableAmount(CardsConstants.NEW_CARD_LIMIT)
        .build();
  }

  @Override
  public CardDto fetchCard(String mobileNumber) {
    var card = cardsRepository
        .findByMobileNumber(mobileNumber)
        .orElseThrow(() -> new ResourceNotFoundException("Card", "mobile number", mobileNumber));

    return CardMapper.mapToCardDto(card);
  }

  @Override
  public boolean updateCard(CardDto cardDto) {

    // first check if loan exists
    var card = cardsRepository
        .findByCardNumber(cardDto.getCardNumber())
        .orElseThrow(() -> new ResourceNotFoundException("Card", "card number", cardDto.getCardNumber()));

    // update values as requested
    var updates = modifyCard(card, cardDto);
    cardsRepository.save(updates);

    return true;
  }

  private Card modifyCard(Card card, CardDto cardDto) {

    card.setMobileNumber(cardDto.getMobileNumber());
    card.setCardType(cardDto.getCardType());
    card.setTotalLimit(cardDto.getTotalLimit());
    card.setAmountUsed(cardDto.getAmountUsed());
    card.setAvailableAmount(cardDto.getAvailableAmount());

    return card;
  }

  @Override
  public boolean deleteCard(String mobileNumber) {

    // first check if card exists
    var card = cardsRepository
        .findByMobileNumber(mobileNumber)
        .orElseThrow(() -> new ResourceNotFoundException("Loan", "mobile number", mobileNumber));

    // delete loan by id
    cardsRepository.deleteById(card.getId());

    return true;
  }
}
