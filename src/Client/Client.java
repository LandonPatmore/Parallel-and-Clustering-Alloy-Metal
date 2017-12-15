package Client;

import Global.AlloyAtom;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.ForkJoinPool;

public class Client {
    private Socket socket;
    private ForkJoinPool f = new ForkJoinPool();
    private AlloyAtom[][] chunk;

    private ObjectOutputStream output;
    private ObjectInputStream input;

    public Client() {
        openConnection();
        try {
            output = new ObjectOutputStream(socket.getOutputStream());
            input = new ObjectInputStream(socket.getInputStream());

            invokePool();
            for (int i = 0; i < 2; i++) {
                setChunk();
            }

            output.close();
            input.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setChunk() {
        try {
            chunk = (AlloyAtom[][]) input.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void invokePool() {
        WorkerThread w = new WorkerThread();
        f.invoke(w);
    }

    private void openConnection() {
        try {
            socket = new Socket("localhost", 9000);
        } catch (IOException e) {
            System.out.println("Cannot connect to host.");
            System.exit(-1);
        }
    }

}
