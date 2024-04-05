package com.luizalabs.simple.common.config;

import java.net.URI;

import org.glassfish.hk2.utilities.Binder;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

import com.luizalabs.simple.common.util.AppProperties;
import com.luizalabs.simple.user.repository.UserRepository;
import com.luizalabs.simple.user.repository.impl.UserScyllaDBRepository;
import com.luizalabs.simple.user.service.UserService;
import com.luizalabs.simple.user.service.impl.UserServiceImpl;

public class AppResourceConfig extends ResourceConfig {
    private static AppResourceConfig instance;

    private AppProperties appProperties;
    private ScyllabDBConfig scyllabDBConfig;

    private AppResourceConfig() {
        appProperties = AppProperties.getInstance();
        scyllabDBConfig = new ScyllabDBConfig(appProperties);
        register();
    }

    public static AppResourceConfig getInstance() {
        if (instance == null) {
            instance = new AppResourceConfig();
        }
        return instance;
    }

    protected void register() {
        register(binder())
            .register(new JacksonFeature())
            .packages(true, "com.luizalabs.simple");
    }

    protected Binder binder() {
        return new AbstractBinder() {
            protected @Override void configure() {
                bind(userRepository()).to(UserRepository.class);
                bind(UserServiceImpl.class).to(UserService.class);
            }
        };
    }

    protected UserRepository userRepository() {
        return new UserScyllaDBRepository(scyllabDBConfig);
    }

    public URI uri() {
        return URI.create(appProperties.getProperty("app.url"));
    }
}
