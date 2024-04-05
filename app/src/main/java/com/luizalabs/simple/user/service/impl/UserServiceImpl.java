package com.luizalabs.simple.user.service.impl;

import java.util.List;
import java.util.Optional;

import com.luizalabs.simple.common.util.AppLogger;
import com.luizalabs.simple.user.model.User;
import com.luizalabs.simple.user.repository.UserRepository;
import com.luizalabs.simple.user.service.UserService;

import jakarta.inject.Inject;

public class UserServiceImpl implements UserService {
    private static final AppLogger LOGGER = new AppLogger(UserServiceImpl.class);

    private UserRepository userRepository;

    public @Inject UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    public @Override Optional<User> getUser(int id) {
        long start = LOGGER.startLog("getUser({})", id);
        Optional<User> optUser = userRepository.findById(id);
        LOGGER.endLog("getUser({})", start, id);
        return optUser;
    }

    public @Override List<User> getUsers(int limit) {
        long start = LOGGER.startLog("getUsers({})", limit);
        List<User> users = userRepository.findAll(limit);
        LOGGER.endLog("getUsers({})", start, limit);
        return users;
    }
    
    public @Override User saveUser(User user) {
        long start = LOGGER.startLog("saveUser({})", user);
        userRepository.save(user); 
        LOGGER.endLog("saveUser({})", start, user);
        return user;
    }
    
    public @Override void deleteUser(int id) {
        long start = LOGGER.startLog("deleteUser({})", id);
        userRepository.deleteById(id);
        LOGGER.endLog("deleteUser({})", start, id);
    }
}
