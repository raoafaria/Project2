package io.grpc.agecalculator;

import io.grpc.Grpc;
import io.grpc.InsecureServerCredentials;
import io.grpc.Server;
import io.grpc.stub.StreamObserver;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;


public class AgeCalculatorServer {
  private static final Logger logger = Logger.getLogger(AgeCalculatorServer.class.getName());

  private Server server;

  private void start() throws IOException {
    int port = 50051;
    server = Grpc.newServerBuilderForPort(port, InsecureServerCredentials.create())
        .addService(new AgeCalculatorImpl())
        .build()
        .start();
    logger.info("Server started, listening on " + port);
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        System.err.println("*** shutting down gRPC server since JVM is shutting down");
        try {
          AgeCalculatorServer.this.stop();
        } catch (InterruptedException e) {
          e.printStackTrace(System.err);
        }
        System.err.println("*** server shut down");
      }
    });
  }

  private void stop() throws InterruptedException {
    if (server != null) {
      server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
    }
  }

  private void blockUntilShutdown() throws InterruptedException {
    if (server != null) {
      server.awaitTermination();
    }
  }

  public static void main(String[] args) throws IOException, InterruptedException {
    final AgeCalculatorServer server = new AgeCalculatorServer();
    server.start();
    server.blockUntilShutdown();
  }

  private class AgeCalculatorImpl extends AgeCalculatorGrpc.AgeCalculatorImplBase {

    @Override
    public void tellAge(AgeRequest req, StreamObserver<AgeReply> responseObserver) {
      AgeReply reply = AgeReply.newBuilder().setMessage("You are " + (2024-req.getBirthYear()) + " years old.").build();
      responseObserver.onNext(reply);
      responseObserver.onCompleted();
    }
  }
}
