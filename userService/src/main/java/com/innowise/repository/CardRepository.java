package com.innowise.repository;

import com.innowise.model.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CardRepository extends JpaRepository<Card, Long> {
    @Query("SELECT c FROM Card c WHERE c.id IN :ids")
    List<Card> findCardsByIds(@Param("ids") List<Long> ids);

    @Query(value = "SELECT * FROM Card WHERE number = :number", nativeQuery = true)
    Card findByNumberNative(@Param("number") String number);
}
