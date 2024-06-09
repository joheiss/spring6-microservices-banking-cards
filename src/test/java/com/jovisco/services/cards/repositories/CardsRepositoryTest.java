package com.jovisco.services.cards.repositories;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import com.jovisco.services.cards.constants.CardsConstants;
import com.jovisco.services.cards.entities.Card;

import jakarta.transaction.Transactional;

@SpringBootTest
public class CardsRepositoryTest {

  @Autowired
  CardsRepository cardsRepository;

  String mobileNumber;

  Card testCard;

  @BeforeEach
  void setUp() {
    mobileNumber = "+122234567890";
    testCard = buildCard();
    cardsRepository.save(testCard);
  }

  @Transactional
  @Rollback
  @Test
  void testFindByMobileNumber() {

    assertTrue(cardsRepository.findByMobileNumber(mobileNumber).isPresent());
  }

  @Transactional
  @Rollback
  @Test
  void testFindByMobileNumberNotFound() {

    assertFalse(cardsRepository.findByMobileNumber("+122234567899").isPresent());
  }

  @Transactional
  @Rollback
  @Test
  void testFindByCardNumber() {

    assertTrue(cardsRepository.findByCardNumber(testCard.getCardNumber()).isPresent());
  }

  @Transactional
  @Rollback
  @Test
  void testFindByCardNumberNotFound() {

    var notExistingCardNumber = "999999999999";
    assertFalse(cardsRepository.findByCardNumber(notExistingCardNumber).isPresent());
  }

  private Card buildCard() {
    return Card.builder()
        .mobileNumber(mobileNumber)
        .cardNumber("123456789012")
        .cardType(CardsConstants.CREDIT_CARD)
        .totalLimit(10000)
        .amountUsed(4000)
        .availableAmount(6000)
        .build();
  }
}
