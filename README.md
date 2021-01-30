# Create Simple Microservice With gRPC

### Prerequisites
* [Spring Initializr](https://start.spring.io/)
* [gRPC Server](https://mvnrepository.com/artifact/net.devh/grpc-spring-boot-starter/2.5.1.RELEASE)
* [gRPC Client](https://mvnrepository.com/artifact/net.devh/grpc-client-spring-boot-autoconfigure/2.5.1.RELEASE)
* [Lombok Annotation](https://mvnrepository.com/artifact/org.projectlombok/lombok/1.18.16)

### What is gRPC?
![gRPC Modelling](gRPC-model.svg)

In gRPC, a client application can directly call a method on a server application on a different machine as if it were a local object, making it easier for you to create distributed applications and services. As in many RPC systems, gRPC is based around the idea of defining a service, specifying the methods that can be called remotely with their parameters and return types. On the server side, the server implements this interface and runs a gRPC server to handle client calls. On the client side, the client has a stub (referred to as just a client in some languages) that provides the same methods as the server.

### gRPC Server and Client with Java (Spring Boot)

**Start with spring initializr**

I'm depending [Spring Initializr](https://start.spring.io/) for this as it is much easier. And we have to create two spring boot projects and started with maven project also use Lombok plugins.

- gRPC Server-One
- gRPC Server-Two

**gRPC Server One as a Server**

Add below dependencies on your `pom.xml`

```xml
<!-- For the gRPC server -->
<dependency>
	<groupId>net.devh</groupId>
	<artifactId>grpc-server-spring-boot-starter</artifactId>
	<version>2.5.1.RELEASE</version>
	<exclusions>
		<exclusion>
			<groupId>io.grpc</groupId>
			<artifactId>grpc-netty-shaded</artifactId>
		</exclusion>
	</exclusions>
</dependency>

<!-- For the gRPC client -->
<dependency>
	<groupId>net.devh</groupId>
	<artifactId>grpc-client-spring-boot-autoconfigure</artifactId>
	<version>2.5.1.RELEASE</version>
	<type>pom</type>
</dependency>

<!-- https://mvnrepository.com/artifact/org.projectlombok/lombok -->
<dependency>
	<groupId>org.projectlombok</groupId>
	<artifactId>lombok</artifactId>
	<version>1.18.16</version>
	<scope>provided</scope>
</dependency>
```

Add below extension and plugins into build section

```xml
<extensions>
	<extension>
		<groupId>kr.motd.maven</groupId>
		<artifactId>os-maven-plugin</artifactId>
		<version>1.6.1</version>
	</extension>
</extensions>
<plugins>
	<plugin>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-maven-plugin</artifactId>
	</plugin>
	<plugin>
		<groupId>org.xolstice.maven.plugins</groupId>
		<artifactId>protobuf-maven-plugin</artifactId>
		<version>0.6.1</version>
		<configuration>
			<protocArtifact>
				com.google.protobuf:protoc:3.3.0:exe:${os.detected.classifier}
			</protocArtifact>
			<pluginId>grpc-java</pluginId>
			<pluginArtifact>
				io.grpc:protoc-gen-grpc-java:1.4.0:exe:${os.detected.classifier}
			</pluginArtifact>
			<protoSourceRoot>src/main/proto</protoSourceRoot>
		</configuration>
		<executions>
			<execution>
				<goals>
					<goal>compile</goal>
					<goal>compile-custom</goal>
				</goals>
			</execution>
		</executions>
	</plugin>
</plugins>
```

Add configuration server port and gRPC port number. Create `project/application.yml`.

```js
server:
  port: 8080
grpc:
  server:
    port: 9090
grpcServerTwo:
  host: localhost
  port: 9091
```

The line of `grpcServerTwo` used to communication channel from `gRPC Server-One` into `gRPC Server-Two`.

Then create a protocol buffers on gRPC Server-One `src/main/proto/HelloServiceServerOne.proto` file like below.

```proto3
syntax = "proto3";
option java_multiple_files = true;
package com.example.grpc.serverone.server;

message HelloRequest {
    string firstName = 1;
    string lastName = 2;
}

message HelloResponse {
    string greeting = 1;
}

service HelloServiceServerOne {
    rpc hello(HelloRequest) returns (HelloResponse);
}
```

Once the proto file is created, we should package the project. It will generated the classes inside the target folder.

```bash
mvn clean package -Dmaven.test.skip=true
```

Create an implementation class `com.example.grpc.serverone.server.HelloServiceServerOneImpl` for the proto service.

```java
@GrpcService
public class HelloServiceServerOneImpl extends HelloServiceServerOneGrpc.HelloServiceServerOneImplBase {

    @Override
    public void hello(HelloRequest request, StreamObserver<HelloResponse> responseObserver) {
        String greeting = new StringBuilder()
                .append("Hello, ")
                .append(request.getFirstName())
                .append(" ")
                .append(request.getLastName())
                .append(". This response from server one.")
                .toString();

        HelloResponse response = HelloResponse.newBuilder()
                .setGreeting(greeting)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
```

Update project main application like below.

```java
public class ServerOneApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(ServerOneApplication.class, args);
	}

}
```

Try to run by typing `mvn spring-boot:run` to run the gRPC Server-One.

**gRPC Server Two as a Client**

Add below dependencies on your `pom.xml`

```xml
<!-- For the gRPC server -->
<dependency>
	<groupId>net.devh</groupId>
	<artifactId>grpc-server-spring-boot-starter</artifactId>
	<version>2.5.1.RELEASE</version>
	<exclusions>
		<exclusion>
			<groupId>io.grpc</groupId>
			<artifactId>grpc-netty-shaded</artifactId>
		</exclusion>
	</exclusions>
</dependency>

<!-- For the gRPC client -->
<dependency>
	<groupId>net.devh</groupId>
	<artifactId>grpc-client-spring-boot-autoconfigure</artifactId>
	<version>2.5.1.RELEASE</version>
	<type>pom</type>
</dependency>

<!-- https://mvnrepository.com/artifact/org.projectlombok/lombok -->
<dependency>
	<groupId>org.projectlombok</groupId>
	<artifactId>lombok</artifactId>
	<version>1.18.16</version>
	<scope>provided</scope>
</dependency>
```

Add below extension and plugins into build section

```xml
<extensions>
	<extension>
		<groupId>kr.motd.maven</groupId>
		<artifactId>os-maven-plugin</artifactId>
		<version>1.6.1</version>
	</extension>
</extensions>
<plugins>
	<plugin>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-maven-plugin</artifactId>
	</plugin>
	<plugin>
		<groupId>org.xolstice.maven.plugins</groupId>
		<artifactId>protobuf-maven-plugin</artifactId>
		<version>0.6.1</version>
		<configuration>
			<protocArtifact>
				com.google.protobuf:protoc:3.3.0:exe:${os.detected.classifier}
			</protocArtifact>
			<pluginId>grpc-java</pluginId>
			<pluginArtifact>
				io.grpc:protoc-gen-grpc-java:1.4.0:exe:${os.detected.classifier}
			</pluginArtifact>
			<protoSourceRoot>src/main/proto</protoSourceRoot>
		</configuration>
		<executions>
			<execution>
				<goals>
					<goal>compile</goal>
					<goal>compile-custom</goal>
				</goals>
			</execution>
		</executions>
	</plugin>
</plugins>
```

Add configuration server port and gRPC port number. Create `project/application.yml`.

```js
server:
  port: 8081
grpc:
  server:
    port: 9091
grpcServerOne:
  host: localhost
  port: 9090
```

The line of `grpcServerOne` used to communication channel from `gRPC Server-Two` into `gRPC Server-One`.

Then create a protocol buffers on gRPC Server-Two `src/main/proto/HelloServiceServerOne.proto` file like below.

**Note:** It should be same for protocol buffers on gRPC Server One because we need a stub service from server one.

```proto3
syntax = "proto3";
option java_multiple_files = true;
package com.example.grpc.serverone.server;

message HelloRequest {
    string firstName = 1;
    string lastName = 2;
}

message HelloResponse {
    string greeting = 1;
}

service HelloServiceServerOne {
    rpc hello(HelloRequest) returns (HelloResponse);
}
```

Once the proto file is created, we should package the project. It will generated the classes inside the target folder.

```bash
mvn clean package -Dmaven.test.skip=true
```

Create a service class `com.example.grpc.servertwo.client.GRPCClientService`

```java
@Service
public class GRPCClientService {

    @Value("${grpcServerOne.port:9090}")
    private int grpcServerOnePort;

    @Value("${grpcServerOne.host:localhost}")
    private String grpcServerOneHost;

    private final static Logger log = LoggerFactory.getLogger(GRPCClientService.class);

    public HelloResponse getGreetingFromServerOne(HelloClientRequest request) {
        log.info("### Start execute GRPCClientService from server two");
        log.info(String.format("## grpcServerOneHost::%s and grpcServerPort::%s", grpcServerOneHost, grpcServerOnePort));
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
```

Lets make a rest service that we can request from postman to get response from gRPC Server-One.

Create a request `com.example.grpc.servertwo.rest.dto.HelloClientRequest` we will use `dto` package (Data Transfer Object).

```java
@Data
public class HelloClientRequest {

    private String firstName;

    private String lastName;

}
```

Create a response `com.example.grpc.servertwo.rest.dto.HelloClientResponse`

```java
@Data
@Builder
public class HelloClientResponse {

    private String greeting;

}
```

After that, we will create a rest controller

`com.example.grpc.servertwo.rest.controller.ServerTwoController`

```java
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
```

Update project main application like below.

```java
@SpringBootApplication
public class ServerTwoApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(ServerTwoApplication.class, args);
	}

}
```

Try to run by typing `mvn spring-boot:run` to run the gRPC Server-Two.

**Lets try hello request from Postman**

`URL: http://localhost:8081/greeting (POST)`

Request

```js
{
    "firstName": "Maverick",
    "lastName": "Johnson"
}
```

Response

```js
{
    "greeting": "Hello, Maverick Johnson. This response from server one."
}
```

And the log is

```bash
2021-01-30 18:01:02.702  INFO 53068 --- [nio-8081-exec-1] c.e.g.s.client.GRPCClientService         : ### Start execute GRPCClientService from server two
2021-01-30 18:01:02.702  INFO 53068 --- [nio-8081-exec-1] c.e.g.s.client.GRPCClientService         : ## grpcServerOneHost::localhost and grpcServerOnePort::9090
```

**gRPC Server Two as a Server**

Create a protocol buffers on gRPC Server-Two `src/main/proto/UserServiceServerTwo.proto` file like below.

```proto3
syntax = "proto3";
option java_multiple_files = true;
package com.example.grpc.servertwo.server;

message UserRequest {
    string username = 1;
    string password = 2;
    string fullName = 3;
}

message User {
    string username = 1;
    string password = 2;
    string fullName = 3;
    string status = 4;
    string createdAt = 5;
}

message UserResponse {
    repeated User user = 1;
}

service UserServiceServerTwo {
    rpc createUser(UserRequest) returns (User);
    rpc getAll(UserRequest) returns (UserResponse);
}
```

Once the proto file is created, we should package the project. It will generated the classes inside the target folder.

```bash
mvn clean package -Dmaven.test.skip=true
```

Create an implementation class `com.example.grpc.servertwo.server.UserServiceServerTwoImpl` for the proto service.

```java
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
```

Try to run by typing `mvn spring-boot:run` to run the gRPC Server-Two.

**gRPC Server One as a Client**

Then create a protocol buffers on gRPC Server-One `src/main/proto/UserServiceServerTwo.proto` file like below.

**Note:** It should be same for protocol buffers on gRPC Server Two because we need a stub service from server two.

```proto3
syntax = "proto3";
option java_multiple_files = true;
package com.example.grpc.servertwo.server;

message UserRequest {
    string username = 1;
    string password = 2;
    string fullName = 3;
}

message User {
    string username = 1;
    string password = 2;
    string fullName = 3;
    string status = 4;
    string createdAt = 5;
}

message UserResponse {
    repeated User user = 1;
}

service UserServiceServerTwo {
    rpc createUser(UserRequest) returns (User);
    rpc getAll(UserRequest) returns (UserResponse);
}
```

Once the proto file is created, we should package the project. It will generated the classes inside the target folder.

```bash
mvn clean package -Dmaven.test.skip=true
```

Create a service class `com.example.grpc.servertwo.client.GRPCClientService`

```java
@Service
public class GRPCClientService {

    private final static Logger log = LoggerFactory.getLogger(GRPCClientService.class);

    @Value("${grpcServerTwo.port:9091}")
    private int grpcServerTwoPort;

    @Value("${grpcServerTwo.host:localhost}")
    private String grpcServerTwoHost;

    public User createUser(UserClientRequest request) {
        log.info("### START execute GRPCClientService from server one");
        log.info(String.format("## grpcServerOneHost::%s and grpcServerPort::%s", grpcServerTwoHost, grpcServerTwoPort));
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
```

Lets make a rest service that we can request from postman to get response from gRPC Server-Two.

Create a request `com.example.grpc.serverone.rest.dto.UserClientRequest` we will use `dto` package (Data Transfer Object).

```java
@Data
public class UserClientRequest {

    private String username;

    private String password;

    private String fullName;
}
```

Create a response `com.example.grpc.serverone.rest.dto.UserClientResponse`

```java
@Data
@Builder
public class UserClientResponse {

    private String username;

    private String password;

    private String fullName;

    private String status;

    private String createdAt;

}
```

After that, we will create a rest controller

`com.example.grpc.serverone.rest.controller.ServerOneController`

```java
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
```

Try to run by typing `mvn spring-boot:run` to run the gRPC Server-One.

**Lets try user request from Postman**

`URL: http://localhost:8080/getAllUser (GET)`

Response

```js
[
    {
        "username": "maverick",
        "password": "12824834",
        "fullName": "Maverick Johnson",
        "status": "active",
        "createdAt": "Sat Jan 30 17:42:24 WIB 2021"
    },
    {
        "username": "admin",
        "password": "2348324274",
        "fullName": "Administrator",
        "status": "active",
        "createdAt": "Sat Jan 30 17:42:24 WIB 2021"
    }
]
```

And the log is

```bash
2021-01-30 18:00:31.317  INFO 53038 --- [nio-8080-exec-2] c.e.g.s.client.GRPCClientService         : ### START execute GRPCClientService from server one
2021-01-30 18:00:31.318  INFO 53038 --- [nio-8080-exec-2] c.e.g.s.client.GRPCClientService         : ## grpcServerTwoHost::localhost and grpcServerTwoPort::9091
2021-01-30 18:00:31.967  INFO 53038 --- [nio-8080-exec-2] c.e.g.s.client.GRPCClientService         : ### END execute GRPCClientService from server one
```

`URL: http://localhost:8080/createUser (POST)`

Request

```js
{
    "username": "johndoe",
    "password": "johndoe123",
    "fullName": "John Doe"
}
```

Response

```js
{
    "username": "johndoe",
    "password": "johndoe123",
    "fullName": "John Doe",
    "status": "active",
    "createdAt": "Sat Jan 30 18:01:59 WIB 2021"
}
```

### gRPC Client with Python

This section is optional, because I curious for another programming language.

Set up protocol buffers `project/HelloServiceServerOne.proto`.

**Note:** It should be same for protocol buffers on gRPC Server One because we need a stub service from server one.

```proto3
syntax = "proto3";
option java_multiple_files = true;
package com.example.grpc.serverone.server;

message HelloRequest {
    string firstName = 1;
    string lastName = 2;
}

message HelloResponse {
    string greeting = 1;
}

service HelloServiceServerOne {
    rpc hello(HelloRequest) returns (HelloResponse);
}
```

Generate gRPC classes for Python

We need some dependencies for gRPC.

```bash
$ pip install grpcio grpcio-tools
$ python -m grpc_tools.protoc -I. --python_out=. --grpc_python_out=. HelloServiceServerOne.proto
```

The files generated will be as follows:

- `HelloServiceServerOne_pb2.py` contains message classes.
- `HelloServiceServerOne_pb2_grpc.py` contains server and client classes

Create a gRPC Client

```python
import grpc

## Import generated classes
import HelloServiceServerOne_pb2, HelloServiceServerOne_pb2_grpc


# Open a gRPC channel
channel = grpc.insecure_channel(target='localhost:9090')


# Create a stub for gRPC client
stub = HelloServiceServerOne_pb2_grpc.HelloServiceServerOneStub(channel)

# Create a valid request
request = HelloServiceServerOne_pb2.HelloRequest(firstName="Maverick", lastName="John Doe")

# Make the call
response = stub.hello(request=request)

# Output response
print(response.greeting)
```

Thats it!

With the server already listening, we simply run our client.

```bash
$ python client.py
Hello, Maverick John Doe. This response from server one.
```

### Clone or Download

You can clone or download this project
```bash
https://github.com/piinalpin/gRPC-example.git
```

### Thankyou

[Medium](https://medium.com/@sajeerzeji44/grpc-for-spring-boot-microservices-bd9b79569772) - GRPC for Spring Boot Microservices

[Semantics3](https://www.semantics3.com/blog/a-simplified-guide-to-grpc-in-python-6c4e25f0c506/) - A simplified guide to gRPC in Python