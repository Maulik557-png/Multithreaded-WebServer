package singlethreaded;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {

    private static final Logger logger = Logger.getLogger(Client.class.getName());
    private static final String HOST = "localhost";
    private static final int PORT = 8081;

    public static void run() throws IOException {

        InetAddress address = InetAddress.getByName(HOST);

        try (
            Socket socket = new Socket(address, PORT);

            PrintWriter toServer = new PrintWriter(socket.getOutputStream(), true);

            BufferedReader fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            String serverGreeting = fromServer.readLine();
            logger.info("Server said: " + serverGreeting);

            toServer.println("Hello from the client!");
        }
    }

    public static void main(String[] args) {
        try {
            run();
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Client encountered an error", ex);
        }
    }
}