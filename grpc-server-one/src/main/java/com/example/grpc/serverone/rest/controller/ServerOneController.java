package com.example.grpc.serverone.rest.controller;

import com.example.grpc.serverone.client.GRPCClientService;
import com.example.grpc.serverone.rest.dto.UserClientRequest;
import com.example.grpc.serverone.rest.dto.UserClientResponse;
import com.example.grpc.servertwo.server.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ServerOneController {

    private final GRPCClientService grpcClientService;

    @Autowired
    public ServerOneController(GRPCClientService grpcClientService) {
        this.grpcClientService = grpcClientService;
    }

    @PostMapping("/createUser")
    public UserClientResponse createUser(@RequestBody UserClientRequest request) {
        User user = grpcClientService.createUser(request);
        return UserClientResponse.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .fullName(user.getFullName())
                .createdAt(user.getCreatedAt())
                .status(user.getStatus())
                .build();
    }

    @GetMapping("/getAllUser")
    public List<UserClientResponse> getAllUser() {
        return grpcClientService.getAllUser();
    }

}
