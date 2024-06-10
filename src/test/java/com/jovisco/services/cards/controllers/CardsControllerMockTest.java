package com.jovisco.services.cards.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jovisco.services.cards.constants.CardsConstants;
import com.jovisco.services.cards.dtos.CardDto;
import com.jovisco.services.cards.dtos.CreateCardDto;
import com.jovisco.services.cards.services.CardsService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CardsController.class)
public class CardsControllerMockTest {

  @Autowired
  MockMvc mockMvc;

  @Autowired
  ObjectMapper objectMapper;

  @MockBean
  CardsService cardsService;

  final String mobileNumber = "+122234567890";

  @Test
  void testCreateCard() throws Exception {

    var createDto = new CreateCardDto(mobileNumber);

    mockMvc.perform(
        post("/api/v1/" + CardsController.CARDS_PATH)
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createDto))
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated())
        .andExpect(header().exists("Location"));

    // verify that the cards service's createCard method was invoked
    verify(cardsService, times(1)).createCard(createDto);

  }

  @Test
  void testDeleteCard() throws Exception {

    given(cardsService.deleteCard(any())).willReturn(true);

    mockMvc.perform(
        delete("/api/v1/" + CardsController.CARDS_MOBILENUMBER_PATH, mobileNumber))
        .andExpect(status().isOk());

    // verify that the cards service's deleteCard method was invoked
    verify(cardsService, times(1)).deleteCard(mobileNumber);
  }

  @Test
  void testFetchCard() throws Exception {

    given(cardsService.fetchCard(any())).willReturn(buildCardDto(mobileNumber));

    mockMvc.perform(
        get("/api/v1/" + CardsController.CARDS_MOBILENUMBER_PATH, mobileNumber)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

    // verify that the cards service's deleteCard method was invoked
    verify(cardsService, times(1)).fetchCard(mobileNumber);
  }

  @Test
  void testUpdateCard() throws Exception {

    given(cardsService.updateCard(any())).willReturn(true);

    var updateDto = buildCardDto(mobileNumber);
    mockMvc.perform(
        put("/api/v1/" + CardsController.CARDS_PATH)
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateDto))
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

    // verify that the cards service's updateCard method was invoked
    verify(cardsService, times(1)).updateCard(updateDto);
  }

  private CardDto buildCardDto(String mobileNumber) {

    return CardDto.builder()
        .mobileNumber(mobileNumber)
        .cardNumber("1234567890123456")
        .cardType(CardsConstants.CREDIT_CARD)
        .totalLimit(CardsConstants.NEW_CARD_LIMIT)
        .amountUsed(0)
        .availableAmount(CardsConstants.NEW_CARD_LIMIT)
        .build();
  }
}
