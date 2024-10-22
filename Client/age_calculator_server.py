import grpc
from concurrent import futures
import age_calculator_pb2
import age_calculator_pb2_grpc
from datetime import datetime

# Implement the AgeCalculator service
class AgeCalculatorServicer(age_calculator_pb2_grpc.AgeCalculatorServicer):
    def TellAge(self, request, context):
        current_year = datetime.now().year
        age = current_year - request.birthYear
        response_message = f'You are {age} years old.'
        return age_calculator_pb2.AgeReply(message=response_message)

# Create and start the server
def serve():
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=10))
    age_calculator_pb2_grpc.add_AgeCalculatorServicer_to_server(AgeCalculatorServicer(), server)
    server.add_insecure_port('[::]:50051')
    print("Server is starting on port 50051...")
    server.start()
    server.wait_for_termination()

if __name__ == '__main__':
    serve()
