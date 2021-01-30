package com.example.grpc.serverone.rest.dto;

import lombok.Data;

@Data
public class UserClientRequest {

    private String username;

    private String password;

    private String fullName;
}
