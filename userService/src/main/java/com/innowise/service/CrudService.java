package com.innowise.service;

import java.util.List;

public interface CrudService<Request,  Response, ID> {
    Response save(Request request);
    Response findById(ID id);
    List<Response> findByIds(List<ID> ids);
    Response updateById(ID id, Request request);
    void deleteById(ID id);
}
