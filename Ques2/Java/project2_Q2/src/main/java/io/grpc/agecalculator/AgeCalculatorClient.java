package io.grpc.agecalculator;

import io.grpc.Channel;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AgeCalculatorClient {
  private static final Logger logger = Logger.getLogger(AgeCalculatorClient.class.getName());

  private final AgeCalculatorGrpc.AgeCalculatorBlockingStub blockingStub;

  public AgeCalculatorClient(Channel channel) {
    blockingStub = AgeCalculatorGrpc.newBlockingStub(channel);
  }

  public void calculate(int birthYear) {
    logger.info("Will try to calculate age for birth year: " + birthYear + " ...");

    AgeRequest request = AgeRequest.newBuilder().setBirthYear(birthYear).build();
    AgeReply response;
    try {
      response = blockingStub.tellAge(request);
    } catch (StatusRuntimeException e) {
      logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
      return;
    }

    logger.info("Calculated Age: " + response.getMessage());
  }

  public static void main(String[] args) throws Exception {
    // String user = "world";
    int birthyear = 2000; //My birth year
    String target = "localhost:50051";
    if (args.length > 0) {
      if ("--help".equals(args[0])) {
        System.err.println("Usage: [birthyear(int) [target]]");
        System.err.println("");
        System.err.println("  birthyear    Your birth year. Defaults to " + birthyear);
        System.err.println("  target  The server to connect to. Defaults to " + target);
        System.exit(1);
      }
      birthyear = Integer.parseInt(args[0]);
    }
    if (args.length > 1) {
      target = args[1];
    }

    ManagedChannel channel = Grpc.newChannelBuilder(target, InsecureChannelCredentials.create())
        .build();
    try {
      AgeCalculatorClient client = new AgeCalculatorClient(channel);
      client.calculate(birthyear);
    } finally {
      channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
    }
  }
}
