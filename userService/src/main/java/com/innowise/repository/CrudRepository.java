package com.innowise.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface CrudRepository<T, ID> extends JpaRepository<T, ID> {
//
//    @Override
//    <S extends T> S create(S entity);
//
//    @Override
//    Optional<T> getById(ID id);
//
//    @Override
//    void deleteById(ID id);

}
