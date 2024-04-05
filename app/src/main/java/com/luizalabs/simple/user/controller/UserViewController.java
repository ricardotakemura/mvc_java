package com.luizalabs.simple.user.controller;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import com.luizalabs.simple.user.model.User;
import com.luizalabs.simple.user.service.UserService;

import jakarta.inject.Inject;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

public @Path("user") class UserViewController {
    private UserService userService;

    public @Inject UserViewController(UserService userService) {
        this.userService = userService;
    }
    
    public @GET @Path("/{id}") @Produces(MediaType.APPLICATION_JSON) Response get(@PathParam("id") int id) {
        Optional<User> optUser = userService.getUser(id);
        if (optUser.isPresent()) {
            return Response.ok().entity(optUser.get()).build();
        }
        return Response.status(Status.NOT_FOUND).build();
    }

    public @GET @Produces(MediaType.APPLICATION_JSON) Response getAll(
        @QueryParam("limit") @DefaultValue("10") int limit) {
        List<User> users = userService.getUsers(limit);
        return Response.ok().entity(users).build();
    }

    public @DELETE @Path("/{id}") @Produces(MediaType.APPLICATION_JSON) Response delete(@PathParam("id") int id) {
        userService.deleteUser(id);
        return Response.noContent().build();
    }

    public @POST @Produces(MediaType.APPLICATION_JSON) Response save(User user) {
        user = userService.saveUser(user);
        return Response.created(URI.create("/user/" + user.getId())).build();
    }
}
