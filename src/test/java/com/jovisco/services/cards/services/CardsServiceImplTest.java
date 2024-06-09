package com.jovisco.services.cards.services;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import com.jovisco.services.cards.constants.CardsConstants;
import com.jovisco.services.cards.dtos.CreateCardDto;
import com.jovisco.services.cards.entities.Card;
import com.jovisco.services.cards.exceptions.CardAlreadyExistsException;
import com.jovisco.services.cards.exceptions.ResourceNotFoundException;
import com.jovisco.services.cards.mappers.CardMapper;
import com.jovisco.services.cards.repositories.CardsRepository;

import jakarta.transaction.Transactional;

@SpringBootTest
public class CardsServiceImplTest {

  @Autowired
  CardsService cardsService;

  @Autowired
  CardsRepository cardsRepository;

  Card testCard;

  @BeforeEach
  void setUp() {
    testCard = cardsRepository.save(buildCard());
  }

  @Transactional
  @Rollback
  @Test
  void testCreateCard() {

    // create new card - with other mobile number than the already existing card
    var createDto = new CreateCardDto("+233345678901");
    cardsService.createCard(createDto);

    // check if card has been created
    assertTrue(cardsRepository.findByMobileNumber(createDto.getMobileNumber()).isPresent());
  }

  @Transactional
  @Rollback
  @Test
  void testCreateCardWithAlreadyExistsError() {

    // try to create new card - with same mobile number than the already existing card
    var createDto = new CreateCardDto(testCard.getMobileNumber());

    // check that exception is thrown
    assertThatExceptionOfType(CardAlreadyExistsException.class)
        .isThrownBy(() -> cardsService.createCard(createDto));

  }

  @Transactional
  @Rollback
  @Test
  void testDeleteCard() {
    // test if delete works
    assertTrue(cardsService.deleteCard(testCard.getMobileNumber()));

    // test if card is really deleted from database
    assertFalse(cardsRepository.findByMobileNumber(testCard.getMobileNumber()).isPresent());
  }

  @Transactional
  @Rollback
  @Test
  void testDeleteCardWithNotFoundError() {

    // first delete test card
    cardsService.deleteCard(testCard.getMobileNumber());

    // check that exception is thrown
    assertThatExceptionOfType(ResourceNotFoundException.class)
        .isThrownBy(() -> cardsService.deleteCard(testCard.getMobileNumber()));

  }

  @Transactional
  @Rollback
  @Test
  void testFetchCard() {

    var cardDto = cardsService.fetchCard(testCard.getMobileNumber());

    assertThat(cardDto).isNotNull();
    assertThat(cardDto.getMobileNumber()).isEqualTo(testCard.getMobileNumber());
  }

  @Transactional
  @Rollback
  @Test
  void testFetchCardWithNotFoundError() {

    // first delete test card
    cardsService.deleteCard(testCard.getMobileNumber());

    assertThatExceptionOfType(ResourceNotFoundException.class)
        .isThrownBy(() -> cardsService.fetchCard(testCard.getMobileNumber()));
  }

  @Transactional
  @Rollback
  @Test
  void testUpdateCard() {

    // change some card data
    var cardDto = CardMapper.mapToCardDto(testCard);
    cardDto.setTotalLimit(33333);
    cardDto.setAmountUsed(22222);
    cardDto.setAvailableAmount(11111);

    assertTrue(cardsService.updateCard(cardDto));

    // check for correctly updated values
    var optionalCard = cardsRepository.findByMobileNumber(cardDto.getMobileNumber());
    assertTrue(optionalCard.isPresent());
    var card = optionalCard.get();
    assertTrue(card.getTotalLimit() == cardDto.getTotalLimit() &&
        card.getAmountUsed() == cardDto.getAmountUsed() &&
        card.getAvailableAmount() == cardDto.getAvailableAmount());
  }

  @Transactional
  @Rollback
  @Test
  void testUpdateCardWithNotFoundError() {

    // change some card data
    var cardDto = CardMapper.mapToCardDto(testCard);
    cardDto.setTotalLimit(33333);
    cardDto.setCardNumber("DOESNOTEXISTCARD");

    assertThatExceptionOfType(ResourceNotFoundException.class)
        .isThrownBy(() -> cardsService.updateCard(cardDto));
  }

  private Card buildCard() {
    return Card.builder()
        .mobileNumber("+122234567890")
        .cardNumber("1234567890123456")
        .cardType(CardsConstants.CREDIT_CARD)
        .totalLimit(10000)
        .amountUsed(4000)
        .availableAmount(6000)
        .build();
  }
}
