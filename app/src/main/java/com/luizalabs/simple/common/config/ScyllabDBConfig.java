package com.luizalabs.simple.common.config;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;

import com.datastax.oss.driver.api.core.CqlSession;
import com.luizalabs.simple.common.util.AppProperties;
import com.luizalabs.simple.common.util.Converter;

public class ScyllabDBConfig {
    private CqlSession session;
    private AppProperties appProperties;

    public ScyllabDBConfig(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    public CqlSession getSession() {
        if (session == null || session.isClosed()) {
            String[] endpoints = appProperties.getPropertyAsArray("app.database.scylladb.endpoints");
            List<InetSocketAddress> contactPoints = Arrays
                .stream(endpoints)
                .map(Converter::inetSocketAddress)
                .toList();
            session = CqlSession.builder()
                .addContactPoints(contactPoints)
                .withLocalDatacenter(appProperties.getProperty("app.database.scylladb.localdatacenter"))
                .withKeyspace(appProperties.getProperty("app.database.scylladb.keyspace"))
                .build();
        }
        return session;
    }

    public boolean canCreateTable() {
        return appProperties.getPropertyAsBoolean("app.database.scylladb.autocreatetable");
    }
}
