package com.luizalabs.simple.user.service;

import java.util.List;
import java.util.Optional;

import com.luizalabs.simple.user.model.User;

public interface UserService {
    Optional<User> getUser(int id);
    List<User> getUsers(int limit);
    User saveUser(User user);
    void deleteUser(int id);
}
