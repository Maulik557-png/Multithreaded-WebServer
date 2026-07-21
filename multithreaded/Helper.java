package multithreaded;

import java.io.IOException;

public class Helper {
    public static void main(String[] args) throws InterruptedException {
        Server server = new Server();

        // Server.run() loops forever accepting connections, so it needs its own
        // thread - otherwise main() would block here and never reach the client.
        Thread serverThread = new Thread(() -> {
            try {
                server.run();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, "server-loop");
        serverThread.setDaemon(true);
        serverThread.start();

        // Give the server a moment to bind and start listening before clients
        // try to connect. A production version would use a readiness signal
        // (e.g. a CountDownLatch) instead of a sleep.
        Thread.sleep(500);

        new Client().run();
    }
}
