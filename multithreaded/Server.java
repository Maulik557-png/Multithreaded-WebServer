package multithreaded;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class Server {

    public Consumer<Socket> getConsumer() {
        return (clientSocket) -> {
            try (
                    PrintWriter toClient = new PrintWriter(clientSocket.getOutputStream(), true);
                    BufferedReader fromClient = new BufferedReader(
                            new InputStreamReader(clientSocket.getInputStream()));) {

                toClient.println("Hello from the server!");
                System.out.println("Client Response: " + fromClient.readLine());
                clientSocket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }

    public void run() throws IOException {
        int port = 8081;
        ExecutorService threadPool = Executors.newFixedThreadPool(500);

        Server server = new Server();
        ServerSocket socket = new ServerSocket(port, 1000);
        System.out.println("Server is listening on port " + port);

        try {
            while (true) {
                Socket acceptedConnection = socket.accept();
                acceptedConnection.setSoTimeout(5000);

                threadPool.submit(() -> server.getConsumer().accept(acceptedConnection));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket.close();
            threadPool.shutdown();
        }
    }
}
