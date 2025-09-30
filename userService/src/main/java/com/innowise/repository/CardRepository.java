package com.innowise.repository;

import com.innowise.model.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    List<Card> getByUserId(Long userId);

    @Query("SELECT c FROM Card c WHERE c.id IN :ids")
    List<Card> getCardByIds(List<Long> ids);

    @Modifying
    @Query(value = "DELETE FROM Card AS c WHERE c.id = :id", nativeQuery = true)
    void deleteCardByIdNative(Long id);
}
