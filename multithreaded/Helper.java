package multithreaded;

import java.io.IOException;

public class Helper {
    public static void main(String[] args) throws IOException, InterruptedException {
        try {
            new Server().run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
