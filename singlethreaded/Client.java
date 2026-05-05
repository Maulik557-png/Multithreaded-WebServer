package singlethreaded;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class Client {
    public static void run() throws IOException {
        int port = 8081;
        InetAddress address = InetAddress.getByName("localhost");
        Socket socket = new Socket(address, port);
        PrintWriter toServer = new PrintWriter(socket.getOutputStream());
        BufferedReader fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        toServer.println("Hello from the client");
        System.out.println("Server Response: " + fromServer.readLine());
        toServer.close();
        fromServer.close();
        socket.close();
    }

    public static void main(String[] args) {
        try {
            run();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
