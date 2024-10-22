package com.example.agecalculator;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

public class AgeCalculatorServer {
    public static void main(String[] args) throws Exception {
        // Create and start the gRPC server
        Server server = ServerBuilder.forPort(50051)
                .addService(new AgeCalculatorImpl())
                .build()
                .start();

        System.out.println("Server started, listening on " + 50051);
        server.awaitTermination();
    }

    // Implement the service
    static class AgeCalculatorImpl extends AgeCalculatorGrpc.AgeCalculatorImplBase {
        @Override
        public void tellAge(AgeCalculatorProto.AgeRequest request, StreamObserver<AgeCalculatorProto.AgeReply> responseObserver) {
            // Calculate the age
            int currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
            int age = currentYear - request.getBirthYear();
            AgeCalculatorProto.AgeReply reply = AgeCalculatorProto.AgeReply.newBuilder()
                    .setMessage("Your age is: " + age)
                    .build();
            // Send the response
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }
    }
}
