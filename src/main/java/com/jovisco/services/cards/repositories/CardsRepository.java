package com.jovisco.services.cards.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jovisco.services.cards.entities.Card;

@Repository
public interface CardsRepository extends JpaRepository<Card, Long> {

  Optional<Card> findByMobileNumber(String mobileNumber);

  Optional<Card> findByCardNumber(String cardNumber);
}
