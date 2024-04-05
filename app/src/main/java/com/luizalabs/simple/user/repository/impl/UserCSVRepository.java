package com.luizalabs.simple.user.repository.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.luizalabs.simple.common.util.AppLogger;
import com.luizalabs.simple.common.util.AppProperties;
import com.luizalabs.simple.user.model.User;
import com.luizalabs.simple.user.repository.UserRepository;

public class UserCSVRepository implements UserRepository {
    private static final AppLogger LOGGER = new AppLogger(UserCSVRepository.class);

    private List<User> users;
    private File file;

    public UserCSVRepository(AppProperties appProperties) {
        users = new ArrayList<>();
        file = new File(appProperties.getProperty("app.database.csv.user.file"));
        if (file.exists()) {
            try (Stream<String> lines = Files.lines(file.toPath())) {
                users = lines.map(this::parse).collect(Collectors.toList());
            } catch (IOException e) {
                LOGGER.error("Error in load data", e);
            }
        }
    }

    public @Override Optional<User> findById(Integer id) {
        return users.stream().filter(it -> it.getId() == id).findFirst();
    }

    public @Override List<User> findAll(Integer limit) {
        if (users.size() > limit) {
            return users.subList(0, limit);
        }
        return users;
    }

    public @Override boolean save(User user) {
        if (user.getId() == null) {
            user.setId(users.size() + 1);
        }
        Optional<User> found = users.stream()
            .filter(it -> it.getId() == user.getId())
            .findFirst();
        found.ifPresentOrElse(it -> {
            it.setName(user.getName());
            it.setSurname(user.getSurname());
        }, () -> {
            users.add(user);
        });
        save();
        return true;
    }

    public @Override boolean deleteById(Integer id) {
        users.removeIf(it -> it.getId() == id);
        save();
        return true;
    }

    private synchronized void save() {
        new Thread(() -> {
            try {
                String data = users.stream()
                    .map(this::stringify)
                    .collect(Collectors.joining("\n"));
                Files.write(file.toPath(), data.getBytes(), StandardOpenOption.WRITE);
            } catch (IOException e) {
                LOGGER.error("Error in save data", e);
            }                
        }).start();
    }

    private User parse(String line) {
        String[] data = line.split(",");
        return new User(Integer.parseInt(data[0]), data[1], data[2]);
    }

    private String stringify(User user) {
        return Optional.ofNullable(user.getId()).orElse(0) + "," + 
            Optional.ofNullable(user.getName()).orElse("") + "," +
            Optional.ofNullable(user.getSurname()).orElse("");
    }
}
