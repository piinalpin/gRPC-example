package com.example.grpc.serverone.client;

import com.example.grpc.serverone.rest.dto.UserClientRequest;
import com.example.grpc.serverone.rest.dto.UserClientResponse;
import com.example.grpc.servertwo.server.User;
import com.example.grpc.servertwo.server.UserRequest;
import com.example.grpc.servertwo.server.UserResponse;
import com.example.grpc.servertwo.server.UserServiceServerTwoGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GRPCClientService {

    private final static Logger log = LoggerFactory.getLogger(GRPCClientService.class);

    @Value("${grpcServerTwo.port:9091}")
    private int grpcServerTwoPort;

    @Value("${grpcServerTwo.host:localhost}")
    private String grpcServerTwoHost;

    public User createUser(UserClientRequest request) {
        log.info("### START execute GRPCClientService from server one");
        log.info(String.format("## grpcServerTwoHost::%s and grpcServerTwoPort::%s", grpcServerTwoHost, grpcServerTwoPort));
        ManagedChannel channel = ManagedChannelBuilder.forAddress(grpcServerTwoHost, grpcServerTwoPort).usePlaintext().build();
        UserServiceServerTwoGrpc.UserServiceServerTwoBlockingStub stub = UserServiceServerTwoGrpc.newBlockingStub(channel);

        User user = stub.createUser(UserRequest.newBuilder()
                .setUsername(request.getUsername())
                .setPassword(request.getPassword())
                .setFullName(request.getFullName())
                .build());

        channel.shutdown();
        log.info("### END execute GRPCClientService from server one");
        return user;
    }

    public List<UserClientResponse> getAllUser() {
        log.info("### START execute GRPCClientService from server one");
        log.info(String.format("## grpcServerTwoHost::%s and grpcServerTwoPort::%s", grpcServerTwoHost, grpcServerTwoPort));
        ManagedChannel channel = ManagedChannelBuilder.forAddress(grpcServerTwoHost, grpcServerTwoPort).usePlaintext().build();
        UserServiceServerTwoGrpc.UserServiceServerTwoBlockingStub stub = UserServiceServerTwoGrpc.newBlockingStub(channel);

        List<UserClientResponse> response = new ArrayList<>();

        UserResponse grpcResponse = stub.getAll(UserRequest.newBuilder().build());
        channel.shutdown();

        for (User user : grpcResponse.getUserList()) {
            UserClientResponse userResponse = UserClientResponse.builder()
                    .createdAt(user.getCreatedAt())
                    .fullName(user.getFullName())
                    .password(user.getPassword())
                    .status(user.getStatus())
                    .username(user.getUsername())
                    .build();
            response.add(userResponse);
        }
        log.info("### END execute GRPCClientService from server one");

        return response;
    }

}
