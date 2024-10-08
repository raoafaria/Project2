import grpc

import raoa_pb2
import raoa_pb2_grpc

def run():
    # Open a gRPC channel to the server
    with grpc.insecure_channel('localhost:50051') as channel:
        stub = raoa_pb2_grpc.AgeCalculatorStub(channel)
        # Send a request
        response = stub.TellAge(raoa_pb2.AgeRequest(birthYear=1997))
        print("Greeter client received: " + response.message)

if __name__ == '__main__':
    run()
