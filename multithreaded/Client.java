package multithreaded;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class Client {

    private static final int PORT = 8081;
    // Keep this well under ~64,000: a single client machine can only hold
    // that many simultaneous connections to one destination, no matter how
    // cheap the threads are, due to the finite ephemeral local-port range.
    private static final int CLIENT_COUNT = 10_000;

    public Runnable getRunnable() {
        return () -> {
            try (
                    Socket socket = new Socket(InetAddress.getByName("localhost"), PORT);
                    PrintWriter toServer = new PrintWriter(socket.getOutputStream(), true);
                    BufferedReader fromServer = new BufferedReader(
                            new InputStreamReader(socket.getInputStream()))) {

                System.out.println("Server Response: " + fromServer.readLine());
                toServer.println("Hello from the client");

            } catch (IOException e) {
                // This runs on the worker thread, so it must be handled here -
                // a try/catch around thread.start() in run() would never see it.
                System.err.println("Client failed to talk to server: " + e.getMessage());
            }
        };
    }

    public void run() {
        Thread.UncaughtExceptionHandler onFailure = (t, e) ->
                System.err.println("Uncaught exception in " + t.getName() + ": " + e);

        for (int i = 0; i < CLIENT_COUNT; i++) {
            Thread.ofVirtual()
                    .name("client-", 0)
                    .uncaughtExceptionHandler(onFailure)
                    .start(getRunnable());
        }
    }
}
