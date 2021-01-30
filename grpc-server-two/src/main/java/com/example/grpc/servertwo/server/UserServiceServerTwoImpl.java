package com.example.grpc.servertwo.server;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

@GrpcService
public class UserServiceServerTwoImpl extends UserServiceServerTwoGrpc.UserServiceServerTwoImplBase {

    private final static Logger log = LoggerFactory.getLogger(UserServiceServerTwoImpl.class);

    private final static List<User> ALL_USERS = List.of(
            User.newBuilder()
                    .setUsername("maverick")
                    .setPassword("12824834")
                    .setFullName("Maverick Johnson")
                    .setCreatedAt(new Date().toString())
                    .setStatus("active")
                    .build(),
            User.newBuilder()
                    .setUsername("admin")
                    .setPassword("2348324274")
                    .setFullName("Administrator")
                    .setCreatedAt(new Date().toString())
                    .setStatus("active")
                    .build()
    );

    @Override
    public void getAll(UserRequest request, StreamObserver<UserResponse> responseObserver) {
        log.info("### START executing UserServiceServerTwoImpl.getAll()");
        UserResponse response = UserResponse.newBuilder()
                .addAllUser(ALL_USERS)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
        log.info("### END executing UserServiceServerTwoImpl.getAll()");
    }

    @Override
    public void createUser(UserRequest request, StreamObserver<User> responseObserver) {
        log.info("### START executing UserServiceServerTwoImpl.createUser()");
        User user = User.newBuilder()
                .setUsername(request.getUsername())
                .setPassword(request.getPassword())
                .setFullName(request.getFullName())
                .setStatus("active")
                .setCreatedAt(new Date().toString())
                .build();

        responseObserver.onNext(user);
        responseObserver.onCompleted();
        log.info("### END executing UserServiceServerTwoImpl.createUser()");
    }
}
