package singlethreaded;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A simple TCP client that connects to the single-threaded server,
 * reads a greeting, sends a reply, then disconnects.
 */
public class Client {

    private static final Logger logger = Logger.getLogger(Client.class.getName());

    private static final String HOST = "localhost";
    private static final int PORT = 8081;

    public static void run() throws IOException {

        // InetAddress.getByName() resolves the hostname to an IP.
        // Using "localhost" always resolves to 127.0.0.1 (loopback interface),
        // meaning traffic never leaves the machine — ideal for local testing.
        InetAddress address = InetAddress.getByName(HOST);

        // try-with-resources closes Socket (and therefore its streams) on exit.
        // Opening the Socket immediately initiates the TCP three-way handshake
        // with the server. If the server isn't listening, ConnectException is thrown.
        try (
                Socket socket = new Socket(address, PORT);

                // autoFlush=true ensures our message is sent immediately without
                // needing an explicit flush() call.
                PrintWriter toServer = new PrintWriter(socket.getOutputStream(), true);

                BufferedReader fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            // READ the server's greeting first, because Server.java sends first.
            // If you send before reading here, both sides block waiting for the
            // other — a classic deadlock. The order must mirror the server's order.
            String serverGreeting = fromServer.readLine();
            logger.info("Server said: " + serverGreeting);

            // Now send our reply.
            toServer.println("Hello from the client!");
        }
        // Socket closed here — TCP FIN is sent to the server.
    }

    public static void main(String[] args) {
        try {
            run();
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Client encountered an error", ex);
        }
    }
}