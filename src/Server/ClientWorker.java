package Server;

import java.io.*;
import java.net.Socket;

public class ClientWorker implements Runnable {
    private Socket socket;

    public ClientWorker(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            InputStream input = socket.getInputStream();
            OutputStream output = socket.getOutputStream();
            long time = System.currentTimeMillis();
            output.write("Hi there!\n".getBytes());
            output.close();
            input.close();
            System.out.println("Request processed: " + time);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
