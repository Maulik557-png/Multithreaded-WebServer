package singlethreaded;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Logger;

public class ClientHandler {

    private static final Logger logger = Logger.getLogger(ClientHandler.class.getName());
    private final Socket clientSocket;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public void handle() {
        try (
            BufferedReader fromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter toClient = new PrintWriter(clientSocket.getOutputStream(), true);
            Socket ignored = clientSocket
        ) {
            toClient.println("Hello from the server!");
            String clientMessage = fromClient.readLine();

            if (clientMessage != null) {
                logger.info("Client said: " + clientMessage);
            } else {
                logger.warning("Client disconnected without sending a message.");
            }
        } catch (IOException ex) {
            logger.warning("Error handling client " + clientSocket.getRemoteSocketAddress() + ": " + ex.getMessage());
        }
    }
}