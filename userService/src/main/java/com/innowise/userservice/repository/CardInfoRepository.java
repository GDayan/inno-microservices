package com.innowise.userservice.repository;

import com.innowise.userservice.model.entity.CardInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardInfoRepository extends JpaRepository<CardInfo, Long> {
    @Query("SELECT c FROM CardInfo c WHERE c.user.id = :userId")
    List<CardInfo> findByUserId(@Param("userId") Long userId);

    @Query("SELECT c FROM CardInfo c WHERE c.id IN :ids")
    List<CardInfo> findByIds(@Param("ids") List<Long> ids);

    @Modifying
    @Query(value = "DELETE FROM card_info AS c WHERE c.id = :id", nativeQuery = true)
    void deleteByIdNative(@Param("id") Long id);

    @Modifying
    @Query("DELETE FROM CardInfo c WHERE c.user.id = :userId")
    void deleteAllByUserId(@Param("userId") Long userId);
}
