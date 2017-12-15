package Server;

import Global.AlloyAtom;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ClientWorker implements Runnable {
    private Socket socket;
    private AlloyAtom[][] blockA;
    private AlloyAtom[][] blockB;
    private Chunk chunk;

    public ClientWorker(Socket socket, AlloyAtom[][] blockA, AlloyAtom[][] blockB, Chunk chunk) {
        this.socket = socket;
        this.blockA = blockA;
        this.blockB = blockB;
        this.chunk = chunk;
    }

    @Override
    public void run() {
        try {
            InputStream input = socket.getInputStream();
            OutputStream output = socket.getOutputStream();
            output.write(chunk.toString().getBytes());
            output.close();
            input.close();
            System.out.println(socket.getInetAddress().getHostName() + " has connected.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
