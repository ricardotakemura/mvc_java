package com.luizalabs.simple.common.repository;

import java.util.List;
import java.util.Optional;

public interface Repository<T, ID> {
    Optional<T> findById(ID id);
    List<T> findAll(Integer limit);
    boolean save(T user);
    boolean deleteById(ID id);
}
