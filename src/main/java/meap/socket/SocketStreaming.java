package meap.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SocketStreaming {

  private static ExecutorService service = Executors.newFixedThreadPool(200);

  public static void main(String[] args) throws Throwable {
    ServerSocket server = new ServerSocket();
    server.bind(new InetSocketAddress(3000));
    while (true) {
      Socket socket = server.accept();
      //clientHandler(meap.socket).run();
      service.submit(clientHandler(socket));
    }
  }

  private static Runnable clientHandler(Socket socket) {
    return () -> {
      try (
          BufferedReader reader = new BufferedReader(
              new InputStreamReader(socket.getInputStream()));
          PrintWriter writer = new PrintWriter(
              new OutputStreamWriter(socket.getOutputStream()))) {
        String line = "";
        while (!"/quit".equals(line)) {
          line = reader.readLine();
          int length = line.length();
          String response = line + "\n";
          System.out.println("~ " + line);

          System.out.println("Submitting to write the response");
          try {
            TimeUnit.SECONDS.sleep(length);
            System.out.println("Writing response " + response);
            writer.write(response);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }

          writer.flush();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    };
  }
}
