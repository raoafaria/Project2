import grpc
from concurrent import futures
import time

import raoa_pb2
import raoa_pb2_grpc

# Implement the Greeter service
class AgeCalculatorServicer(raoa_pb2_grpc.AgeCalculatorServicer):

    def TellAge(self, request, context):
        response = raoa_pb2.AgeReply()
        response.message = f"You are, {2024 - request.birthYear} years old!"
        return response

# Initialize the server
def serve():
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=10))
    raoa_pb2_grpc.add_AgeCalculatorServicer_to_server(AgeCalculatorServicer(), server)
    server.add_insecure_port('[::]:50051')
    server.start()
    print("Server started. Listening on port 50051...")
    try:
        while True:
            time.sleep(86400)  # Keep the server alive
    except KeyboardInterrupt:
        server.stop(0)

if __name__ == '__main__':
    serve()
