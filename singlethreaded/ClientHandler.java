package singlethreaded;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Logger;

/**
 * Handles a single client connection: reads one line, replies, then closes.
 *
 * Separating this from Server follows the Single Responsibility Principle —
 * Server manages accepting connections; ClientHandler manages talking to one
 * client.
 */
public class ClientHandler {

    private static final Logger logger = Logger.getLogger(ClientHandler.class.getName());

    // The socket representing the connected client.
    // It's final because we assign it once in the constructor and never reassign.
    private final Socket clientSocket;

    /**
     * @param clientSocket the accepted Socket from ServerSocket.accept().
     *                     Must not be null and must be open.
     */
    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    /**
     * Performs the full request/response cycle with the client, then closes
     * all resources.
     *
     * try-with-resources is used so that BufferedReader, PrintWriter, and
     * Socket are all closed in reverse order even if an exception is thrown
     * mid-conversation. This prevents resource leaks.
     */
    public void handle() {

        // try-with-resources: Java closes these in reverse declaration order.
        // Socket is declared last so streams are closed before the socket.
        try (
                // InputStreamReader bridges bytes → characters using the platform
                // default charset. For a POC this is fine; in production you'd
                // specify StandardCharsets.UTF_8 explicitly.
                BufferedReader fromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                // autoFlush=true on PrintWriter means every println() call
                // immediately flushes the underlying stream. Without this flag
                // the text sits in a buffer and may never reach the client.
                PrintWriter toClient = new PrintWriter(clientSocket.getOutputStream(), true);

                // Declaring the socket here ensures it's closed after the streams.
                Socket ignored = clientSocket) {
            // Send a greeting to the client first.
            // println() writes the message + "\r\n", which is the standard
            // line terminator for text-based network protocols.
            toClient.println("Hello from the server!");

            // readLine() blocks until the client sends a line ending in '\n'.
            // Returns null if the client closes the connection without sending.
            String clientMessage = fromClient.readLine();

            if (clientMessage != null) {
                logger.info("Client said: " + clientMessage);
            } else {
                logger.warning("Client disconnected without sending a message.");
            }

        } catch (IOException ex) {
            // Log but don't crash the server — a bad client shouldn't bring
            // down the whole server process.
            logger.warning("Error handling client " + clientSocket.getRemoteSocketAddress() + ": " + ex.getMessage());
        }
        // Streams and socket are auto-closed here.
    }
}