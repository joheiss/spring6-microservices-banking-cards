package com.jovisco.services.cards.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jovisco.services.cards.dtos.CreateCardDto;
import com.jovisco.services.cards.exceptions.CardAlreadyExistsException;
import com.jovisco.services.cards.exceptions.ResourceNotFoundException;
import com.jovisco.services.cards.repositories.CardsRepository;
import com.jovisco.services.cards.services.CardsService;

import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class CardsControllerIT {

  @Autowired
  CardsController cardsController;

  @Autowired
  CardsService cardsService;

  @Autowired
  CardsRepository cardsRepository;

  @Autowired
  WebApplicationContext wac;

  @Autowired
  ObjectMapper objectMapper;

  MockMvc mockMvc;

  final String correlationId = "***TEST***";

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
  }

  @Transactional
  @Rollback
  @Test
  void testCreateAccountMvc() throws Exception {

    var createDto = new CreateCardDto("+122234567890");

    mockMvc.perform(
        post("/api/v1/cards")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createDto)))
        .andExpect(status().isCreated())
        .andReturn();
  }

  @Transactional
  @Rollback
  @Test
  void testCreateCard() {

    var createDto = new CreateCardDto("+122234567890");
    var response = cardsController.createCard(createDto);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    var location = response.getHeaders().getLocation();
    assertThat(location).isNotNull();

    // get id from location & verify that the card is really present on the database
    if (location != null) {
      var segments = location.getPath().split("/");
      var mobileNumber = segments[segments.length - 1];
      assertThat(mobileNumber).isNotNull();
      var card = cardsRepository.findByMobileNumber(mobileNumber);
      assertTrue(card.isPresent());
    }
  }

  @Transactional
  @Rollback
  @Test
  void testCreateCardWithAlreadyExistsError() {

    // first create a test card
    var createDto = new CreateCardDto("+122234567890");
    cardsController.createCard(createDto);

    // ... then try to create the same card again which should throw an exception
    assertThatExceptionOfType(CardAlreadyExistsException.class)
        .isThrownBy(() -> cardsController.createCard(createDto));
  }

  @Transactional
  @Rollback
  @Test
  void testCreateCardWithValidationError() {

    // mobile number is invalid
    var createDto = new CreateCardDto("INVALID");

    // ... should throw an exception
    assertThatExceptionOfType(ConstraintViolationException.class)
        .isThrownBy(() -> cardsController.createCard(createDto))
        .withMessageContaining("mobile");
  }

  @Transactional
  @Rollback
  @Test
  void testDeleteCardMvc() throws Exception {

    // first create a test card
    var createDto = new CreateCardDto("+122234567890");
    cardsController.createCard(createDto);

    mockMvc.perform(
        delete("/api/v1/cards/{mobileNumber}", createDto.getMobileNumber()))
        .andExpect(status().isOk())
        .andReturn();
  }

  @Transactional
  @Rollback
  @Test
  void testDeleteCard() {

    // first create a test card
    var createDto = new CreateCardDto("+122234567890");
    var response = cardsController.createCard(createDto);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    var location = response.getHeaders().getLocation();

    // get id from location and delete account
    var segments = location.getPath().split("/");
    var mobileNumber = segments[segments.length - 1];

    response = cardsController.deleteCard(mobileNumber);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    // verify that the card has been deleted from database
    assertFalse(cardsRepository.findByMobileNumber(mobileNumber).isPresent());
  }

  @Transactional
  @Rollback
  @Test
  void testFetchCardMvc() throws Exception {

    // first create a test card
    var createDto = new CreateCardDto("+122234567890");
    cardsController.createCard(createDto);

    mockMvc.perform(
        get("/api/v1/cards/{mobileNumber}", createDto.getMobileNumber()))
        .andExpect(status().isOk())
        .andReturn();
  }

  @Transactional
  @Rollback
  @Test
  void testFetchCard() {

    // first create a test card
    var createDto = new CreateCardDto("+122234567890");
    cardsController.createCard(createDto);

    // check that card can be fetched
    var response = cardsController.fetchCard(correlationId, createDto.getMobileNumber());
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody().getMobileNumber()).isEqualTo(createDto.getMobileNumber());
  }

  @Test
  void testFetchAccountWithNotFound() {

    // check that an exception is thrown
    assertThatExceptionOfType(ResourceNotFoundException.class)
        .isThrownBy(() -> cardsController.fetchCard(correlationId, "+999999999999"));
  }

  @Transactional
  @Rollback
  @Test
  void testUpdateCardMvc() throws Exception {

    // first create a test card
    var createDto = new CreateCardDto("+122234567890");
    cardsController.createCard(createDto);

    // ... fetch it, and update fields
    var cardDto = cardsController.fetchCard(correlationId, createDto.getMobileNumber()).getBody();
    cardDto.setTotalLimit(33333);
    cardDto.setAmountUsed(22222);
    cardDto.setAvailableAmount(11111);

    mockMvc.perform(
        put("/api/v1/cards")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(cardDto)))
        .andExpect(status().isOk())
        .andReturn();
  }

  @Transactional
  @Rollback
  @Test
  void testUpdateCard() {

    // first create a test card
    var createDto = new CreateCardDto("+122234567890");
    cardsController.createCard(createDto);

    // ... fetch it, and modify fields
    var cardDto = cardsController.fetchCard(correlationId, createDto.getMobileNumber()).getBody();
    cardDto.setTotalLimit(33333);
    cardDto.setAmountUsed(22222);
    cardDto.setAvailableAmount(11111);

    // finally update the card with the modified values, and check that it worked
    var response = cardsController.updateCard(cardDto);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    // check that fields have been updated with the correct values
    var updated = cardsController.fetchCard(correlationId, cardDto.getMobileNumber()).getBody();
    assertThat(updated.getTotalLimit()).isEqualTo(cardDto.getTotalLimit());
    assertThat(updated.getAmountUsed()).isEqualTo(cardDto.getAmountUsed());
    assertThat(updated.getAvailableAmount()).isEqualTo(cardDto.getAvailableAmount());
  }

  @Transactional
  @Rollback
  @Test
  void testUpdateCardWithValidationErrors() {

    // first create a test card
    var createDto = new CreateCardDto("+122234567890");
    cardsController.createCard(createDto);

    // ... fetch it, and modify fields
    var cardDto = cardsController.fetchCard(correlationId, createDto.getMobileNumber()).getBody();
    cardDto.setTotalLimit(-33333);
    cardDto.setAmountUsed(-22222);
    cardDto.setAvailableAmount(-11111);
    cardDto.setCardType("");

    assertThatExceptionOfType(ConstraintViolationException.class)
        .isThrownBy(() -> cardsController.updateCard(cardDto))
        .withMessageContainingAll("totalLimit", "amountUsed", "availableAmount", "cardType");
  }

  @Transactional
  @Rollback
  @Test
  void testUpdateCardWithMoreValidationErrors() {

    // first create a test card
    var createDto = new CreateCardDto("+122234567890");
    cardsController.createCard(createDto);

    // ... fetch it, and modify fields
    var cardDto = cardsController.fetchCard(correlationId, createDto.getMobileNumber()).getBody();
    cardDto.setMobileNumber(null);
    cardDto.setTotalLimit(0);
    cardDto.setCardType(null);

    assertThatExceptionOfType(ConstraintViolationException.class)
        .isThrownBy(() -> cardsController.updateCard(cardDto))
        .withMessageContainingAll("mobileNumber", "totalLimit", "cardType");
  }
}
