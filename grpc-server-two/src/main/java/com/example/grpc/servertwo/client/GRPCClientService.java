package com.example.grpc.servertwo.client;

import com.example.grpc.serverone.server.HelloRequest;
import com.example.grpc.serverone.server.HelloResponse;
import com.example.grpc.serverone.server.HelloServiceServerOneGrpc;
import com.example.grpc.servertwo.rest.dto.HelloClientRequest;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GRPCClientService {

    @Value("${grpcServerOne.port:9090}")
    private int grpcServerOnePort;

    @Value("${grpcServerOne.host:localhost}")
    private String grpcServerOneHost;

    private final static Logger log = LoggerFactory.getLogger(GRPCClientService.class);

    public HelloResponse getGreetingFromServerOne(HelloClientRequest request) {
        log.info("### Start execute GRPCClientService from server two");
        log.info(String.format("## grpcServerOneHost::%s and grpcServerOnePort::%s", grpcServerOneHost, grpcServerOnePort));
        ManagedChannel channel = ManagedChannelBuilder.forAddress(grpcServerOneHost, grpcServerOnePort).usePlaintext().build();
        HelloServiceServerOneGrpc.HelloServiceServerOneBlockingStub stub = HelloServiceServerOneGrpc.newBlockingStub(channel);

        HelloResponse response = stub.hello(HelloRequest.newBuilder()
                .setFirstName(request.getFirstName())
                .setLastName(request.getLastName())
                .build());

        channel.shutdown();

        return response;
    }

}
