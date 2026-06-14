package multithreaded;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class Client {
    public Runnable getRunnable() {
        return () -> {
            int port = 8081;
            try (
                    Socket socket = new Socket(InetAddress.getByName("localhost"), port);
                    PrintWriter toServer = new PrintWriter(socket.getOutputStream(), true);
                    BufferedReader fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));) {
                System.out.println("Server Response: " + fromServer.readLine());
                toServer.println("Hello from the client");
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }

    public void run() throws IOException {
        Client client = new Client();

        for (int i = 0; i < 10; i++) {
            try {
                Thread thread = new Thread(client.getRunnable());
                thread.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
