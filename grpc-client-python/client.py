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