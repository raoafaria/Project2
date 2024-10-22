import grpc
import age_calculator_pb2
import age_calculator_pb2_grpc

def run():
    # Open a gRPC channel
    with grpc.insecure_channel('localhost:50051') as channel:
        stub = age_calculator_pb2_grpc.AgeCalculatorStub(channel)

        # Create request with the birth year
        birth_year = input('Enter birth year: ')
        request = age_calculator_pb2.AgeRequest(birthYear=int(birth_year))

        # Make the call
        response = stub.TellAge(request)
        print(response.message)


if __name__ == '__main__':
    run()
