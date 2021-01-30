package com.example.grpc.servertwo.rest.controller;

import com.example.grpc.serverone.server.HelloResponse;
import com.example.grpc.servertwo.client.GRPCClientService;
import com.example.grpc.servertwo.rest.dto.HelloClientRequest;
import com.example.grpc.servertwo.rest.dto.HelloClientResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ServerTwoController {

    private final GRPCClientService grpcClientService;

    private final static Logger log = LoggerFactory.getLogger(ServerTwoController.class);

    @Autowired
    public ServerTwoController(GRPCClientService grpcClientService) {
        this.grpcClientService = grpcClientService;
    }

    @PostMapping("/greeting")
    public HelloClientResponse greeting(@RequestBody HelloClientRequest request) {
        HelloResponse grpcResponse = grpcClientService.getGreetingFromServerOne(request);
        return HelloClientResponse.builder()
                .greeting(grpcResponse.getGreeting())
                .build();
    }

}
