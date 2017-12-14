package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private ServerSocket server;
    private boolean isStopped = false;
    private int port;

    public Server(int port) {
        this.port = port;
        openServerSocket(port);
    }

    public void startServer() {
        while (!isStopped) {
            Socket clientSocket;
            try {
                clientSocket = server.accept();
            } catch (IOException e) {
                if (isStopped) {
                    System.out.println("Server Stopped.");
                    return;
                }
                throw new RuntimeException(
                        "Error accepting client connection", e);
            }
            new Thread(new ClientWorker(clientSocket)).start();
        }
    }

    private void openServerSocket(int port) {
        try {
            server = new ServerSocket(port);
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port 8080", e);
        }
    }
}
