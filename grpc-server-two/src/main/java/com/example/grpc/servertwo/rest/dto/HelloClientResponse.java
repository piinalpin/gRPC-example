package com.example.grpc.servertwo.rest.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HelloClientResponse {

    private String greeting;

}
