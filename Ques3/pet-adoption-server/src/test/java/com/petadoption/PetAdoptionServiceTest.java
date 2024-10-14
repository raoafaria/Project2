package com.petadoption;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import com.petadoption.PetAdoptionProto.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class PetAdoptionServiceTest {
    private ManagedChannel channel;
    private PetAdoptionServiceGrpc.PetAdoptionServiceBlockingStub stub;
    private Server server;

    @Before
    public void setUp() throws IOException {
        // Start the gRPC server
        server = ServerBuilder.forPort(50051)
                .addService(new PetAdoptionServiceImpl())
                .build()
                .start();

        // Set up the channel and stub to communicate with the server
        channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();
        stub = PetAdoptionServiceGrpc.newBlockingStub(channel);
    }

    @After
    public void tearDown() throws InterruptedException {
        // Clean up: shutdown channel and server
        channel.shutdown();
        if (server != null) {
            server.shutdown();
            server.awaitTermination();
        }
    }

    @Test
    public void testRegisterPet() {
        // Build the request
        RegisterPetRequest request = RegisterPetRequest.newBuilder()
                .setName("Buddy")
                .setGender("Male")
                .setAge(3)
                .setBreed("Golden Retriever")
                .build();

        // Call the gRPC method
        RegisterPetResponse response = stub.registerPet(request);

        // Assert the response
        assertTrue(response.getSuccess());
        assertEquals("Pet registered successfully!", response.getMessage());
    }
}