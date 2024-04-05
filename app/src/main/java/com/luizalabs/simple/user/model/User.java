package com.luizalabs.simple.user.model;

import java.util.Objects;

import com.luizalabs.simple.common.repository.annotation.Column;
import com.luizalabs.simple.common.repository.annotation.Id;
import com.luizalabs.simple.common.repository.annotation.Table;


public @Table("user") class User {
    private @Id @Column("id") Integer id;    
    private @Column("name") String name;    
    private @Column("surname") String surname;

    public User() {
    }

    public User(Integer id,
        String name,
        String surname) {
        this.id = id;
        this.name = name;
        this.surname = surname;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public @Override int hashCode() {
        return Objects.hash(name, surname);
    }
}
