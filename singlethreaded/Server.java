package singlethreaded;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void run() throws IOException{
        int port = 8081;
        ServerSocket socket = new ServerSocket(port);
        socket.setSoTimeout(10000);
        while (true) {
            System.out.println("Server is listening on port " + port);
            Socket acceptedConnection = socket.accept();
            System.out.println("Connection accepted from the client with address " + acceptedConnection.getRemoteSocketAddress());
            ClientHandler handler = new ClientHandler(acceptedConnection);
            handler.handle();
            socket.close();
        }
    }

    public static void main(String[] args) {
        try {
            run();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
