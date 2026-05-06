package singlethreaded;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler {

    private final Socket clientSocket;
    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public void handle() {
        try (
            BufferedReader fromClient = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter toClient = new PrintWriter(
                    clientSocket.getOutputStream(), true);
            Socket ignored = clientSocket
        ) {
            toClient.println("Hello from the server!");
            String clientMessage = fromClient.readLine();
            System.out.println(clientMessage);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}