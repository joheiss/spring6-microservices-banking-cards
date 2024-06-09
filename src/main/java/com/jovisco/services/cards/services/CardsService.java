package com.jovisco.services.cards.services;

import com.jovisco.services.cards.dtos.CardDto;
import com.jovisco.services.cards.dtos.CreateCardDto;

public interface CardsService {

  /**
   * 
   * @param mobileNumber
   */
  void createCard(CreateCardDto createCardDto);

  /**
   * 
   * @param mobileNumber
   * @return card details
   */
  CardDto fetchCard(String mobileNumber);

  /**
   * 
   * @param cardDto
   * @return boolean indicating if update was successful
   */
  boolean updateCard(CardDto cardDto);

  /**
   * 
   * @param mobileNumber
   * @return boolean indicating if delete was successful
   */
  boolean deleteCard(String mobileNumber);
}
