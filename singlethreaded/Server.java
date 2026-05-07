package singlethreaded;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {

    private static final Logger logger = Logger.getLogger(Server.class.getName());
    private static final int PORT = 8081;

    public static void run() throws IOException {

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            logger.info("Single-threaded server started. Listening on port " + PORT);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                logger.info("Connection accepted from: " + clientSocket.getRemoteSocketAddress());
                ClientHandler handler = new ClientHandler(clientSocket);
                handler.handle();
            }
        }
    }

    public static void main(String[] args) {
        try {
            run();
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Server failed to start or encountered a fatal error", ex);
        }
    }
}