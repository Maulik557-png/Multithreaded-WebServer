# Single-Threaded Web Server

This project contains a simple TCP client and server written in Java using only core networking APIs. Despite the repository name, the implementation in the `singlethreaded` folder is a single-threaded server: it accepts one client connection at a time, handles that client fully, then returns to the accept loop for the next connection.

## How It Works

The execution flow is intentionally small and linear:

1. `Server` starts a `ServerSocket` on port `8081`.
2. The server blocks on `accept()` until a client connects.
3. Once a socket is accepted, `Server` creates a `ClientHandler` for that connection.
4. `ClientHandler` sends a greeting message first.
5. The handler then waits for a single line from the client and logs it.
6. The client socket and streams are closed automatically.
7. The server returns to `accept()` and waits for the next client.

The client follows the opposite side of the same conversation:

1. `Client` connects to `localhost:8081`.
2. It reads the server greeting.
3. It sends one reply line back to the server.
4. The connection closes.

Because both sides read and write in the same order, the protocol stays simple and avoids deadlock.

## Project Files

- `singlethreaded/Server.java` starts the server and owns the accept loop.
- `singlethreaded/ClientHandler.java` handles a single request/response exchange for one client.
- `singlethreaded/Client.java` connects to the server, reads the greeting, and sends a response.

## Requirements

- Java 8 or later

## Run It

From the project root, compile the sources:

```bash
javac -d out singlethreaded/*.java
```

Start the server in one terminal:

```bash
java -cp out singlethreaded.Server
```

Then run the client in another terminal:

```bash
java -cp out singlethreaded.Client
```

You should see the server log the incoming connection and the client message, while the client logs the greeting received from the server.

## Notes

- The server listens on port `8081`.
- The implementation uses blocking I/O and handles only one client at a time.
- `ClientHandler` is separated from `Server` to keep connection acceptance and connection handling distinct.
