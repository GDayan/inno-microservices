package com.innowise.service;


import com.innowise.model.dto.request.CardRequest;
import com.innowise.model.dto.response.CardResponse;

import java.util.List;

public interface CardService {
    CardResponse save(CardRequest cardRequest);
    CardResponse findById(Long id);
    List<CardResponse> findByIds(List<Long> ids);
    CardResponse updateById(Long id, CardRequest cardRequest);
    void deleteById(Long id);

}
