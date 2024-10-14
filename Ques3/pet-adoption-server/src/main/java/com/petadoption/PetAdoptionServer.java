package com.petadoption;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class PetAdoptionServer {
    private static final Logger logger = LoggerFactory.getLogger(PetAdoptionServer.class);
    private Server server;
    private ExecutorService executorService;  // Thread pool executor

    // Start the gRPC server
    public void start() throws IOException {
        // Create a fixed thread pool with 10 threads
        executorService = Executors.newFixedThreadPool(10);

        server = ServerBuilder
                .forPort(50051)
                .addService(new PetAdoptionServiceImpl())  // Register your service implementation
                .executor(executorService)  // Use custom executor (multi-threading)
                .build()
                .start();

        logger.info("Server started, listening on " + server.getPort());

        // Add shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutting down gRPC server...");
            PetAdoptionServer.this.stop();
        }));
    }

    // Stop the gRPC server and gracefully shut down the thread pool
    public void stop() {
        if (server != null) {
            server.shutdown();
        }

        // Shutdown the thread pool gracefully
        if (executorService != null) {
            try {
                executorService.shutdown();
                if (!executorService.awaitTermination(30, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                logger.error("Executor interrupted during shutdown", e);
                executorService.shutdownNow();
            }
        }
    }

    // Block the main thread until the server is terminated
    public void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    // Main method to start the server
    public static void main(String[] args) throws IOException, InterruptedException {
        final PetAdoptionServer server = new PetAdoptionServer();
        server.start();
        server.blockUntilShutdown();
    }
}