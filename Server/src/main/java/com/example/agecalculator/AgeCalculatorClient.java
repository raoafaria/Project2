package com.example.agecalculator;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import com.example.agecalculator.AgeCalculatorGrpc.AgeCalculatorBlockingStub;

public class AgeCalculatorClient {

    public static void main(String[] args) {
        // Create a channel to connect to the server
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()  // Disable TLS to allow insecure communication
                .build();

        // Create a stub (blocking - synchronous)
        AgeCalculatorBlockingStub stub = AgeCalculatorGrpc.newBlockingStub(channel);

        // Create a request with the birth year
        int birthYear = 1990; // You can modify this as needed
        AgeCalculatorProto.AgeRequest request = AgeCalculatorProto.AgeRequest.newBuilder()
                .setBirthYear(birthYear)
                .build();

        // Call the service and get the response
        AgeCalculatorProto.AgeReply response = stub.tellAge(request);

        // Print the response
        System.out.println(response.getMessage());

        // Shutdown the channel
        channel.shutdown();
    }
}
