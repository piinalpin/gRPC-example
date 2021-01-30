package com.example.grpc.serverone.rest.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserClientResponse {

    private String username;

    private String password;

    private String fullName;

    private String status;

    private String createdAt;

}
