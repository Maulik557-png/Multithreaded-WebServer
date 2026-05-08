package singlethreaded;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A single-threaded TCP server that accepts one client connection at a time,
 * exchanges a message, then loops back to accept the next client.
 *
 * Design notes:
 * - Single-threaded: each client is fully handled before the next is accepted.
 * - The ServerSocket lives for the lifetime of the server (not closed per
 * connection).
 * - Connection handling is delegated to ClientHandler for separation of
 * concerns.
 */
public class Server {

    // Logger is preferred over System.out for production-grade code.
    // It supports log levels, formatting, and output routing (file, console, etc.)
    private static final Logger logger = Logger.getLogger(Server.class.getName());

    // Port is a named constant — avoids "magic numbers" scattered through the code.
    // 8081 is chosen because port 8080 is commonly occupied; anything above 1024
    // is a safe "user-space" port that doesn't require root/admin privileges.
    private static final int PORT = 8081;

    /**
     * Starts the server and enters an infinite accept loop.
     *
     * @throws IOException if the ServerSocket cannot bind to the port
     *                     (e.g., port already in use).
     */
    public static void run() throws IOException {

        // try-with-resources ensures ServerSocket.close() is called even if an
        // exception escapes the loop. Without this, the OS port stays occupied
        // until the JVM exits (or longer).
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {

            logger.info("Single-threaded server started. Listening on port " + PORT);

            // Infinite loop — the server keeps accepting new clients until the
            // process is killed or an unrecoverable IOException is thrown.
            while (true) {

                // accept() BLOCKS here until a client connects.
                // It returns a new Socket representing that one client connection.
                // The ServerSocket itself stays open and can accept more clients later.
                Socket clientSocket = serverSocket.accept();

                logger.info("Connection accepted from: " + clientSocket.getRemoteSocketAddress());

                // Delegate all per-connection I/O to a dedicated handler class.
                // This keeps the server loop clean and makes ClientHandler
                // independently testable.
                ClientHandler handler = new ClientHandler(clientSocket);
                handler.handle();
            }
        }
        // ServerSocket is auto-closed here by try-with-resources.
    }

    public static void main(String[] args) {
        try {
            run();
        } catch (IOException ex) {
            // log the full stack trace; don't swallow the exception silently.
            logger.log(Level.SEVERE, "Server failed to start or encountered a fatal error", ex);
        }
    }
}