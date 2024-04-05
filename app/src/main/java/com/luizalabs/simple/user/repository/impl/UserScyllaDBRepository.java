package com.luizalabs.simple.user.repository.impl;

import com.luizalabs.simple.common.config.ScyllabDBConfig;
import com.luizalabs.simple.common.repository.scylladb.ScyllaDBRepository;
import com.luizalabs.simple.user.model.User;
import com.luizalabs.simple.user.repository.UserRepository;

public class UserScyllaDBRepository extends ScyllaDBRepository<User, Integer> implements UserRepository {
    public UserScyllaDBRepository(ScyllabDBConfig config) {
        super(config);
    }

    public @Override boolean save(User user) {
        if (user.getId() == null) {
            user.setId(user.hashCode());
        }
        return super.save(user);
    }
}
