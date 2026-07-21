package multithreaded;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class Server {

    private static final int PORT = 8081;
    private static final int BACKLOG = 1000;
    private static final int CLIENT_READ_TIMEOUT_MS = 5000;

    /**
     * Stateless handler for a single accepted connection. Built once and reused
     * across all connections rather than re-created per-connection.
     */
    public Consumer<Socket> getConsumer() {
        return (clientSocket) -> {
            try (
                    clientSocket; // auto-closed when the block exits (Java 9+ resource)
                    PrintWriter toClient = new PrintWriter(clientSocket.getOutputStream(), true);
                    BufferedReader fromClient = new BufferedReader(
                            new InputStreamReader(clientSocket.getInputStream()))) {

                toClient.println("Hello from the server!");
                System.out.println("Client Response: " + fromClient.readLine());

            } catch (SocketTimeoutException e) {
                System.out.println("Client " + clientSocket.getRemoteSocketAddress() + " timed out");
            } catch (IOException e) {
                System.out.println("I/O error handling client "
                        + clientSocket.getRemoteSocketAddress() + ": " + e.getMessage());
            }
        };
    }

    public void run() throws IOException {
        // Virtual threads: blocking socket I/O unmounts the carrier thread while
        // waiting, so we don't need to size or tune a fixed pool anymore.
        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
        Consumer<Socket> handler = getConsumer();

        ServerSocket serverSocket = new ServerSocket(PORT, BACKLOG);
        System.out.println("Server is listening on port " + PORT);

        // Ensure Ctrl+C / kill signals close the listening socket and let
        // in-flight tasks finish instead of dying mid-connection.
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down server...");
            executor.shutdown();
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));

        try {
            while (!serverSocket.isClosed()) {
                Socket acceptedConnection = serverSocket.accept();
                acceptedConnection.setSoTimeout(CLIENT_READ_TIMEOUT_MS);
                executor.submit(() -> handler.accept(acceptedConnection));
            }
        } catch (IOException e) {
            // Expected when the shutdown hook closes the socket to unblock accept().
            if (!serverSocket.isClosed()) {
                e.printStackTrace();
            }
        } finally {
            executor.shutdown();
            if (!serverSocket.isClosed()) {
                serverSocket.close();
            }
        }
    }
}
