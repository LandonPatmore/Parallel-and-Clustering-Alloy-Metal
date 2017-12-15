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

    private ObjectOutputStream output;
    private ObjectInputStream input;

    public Client() {
        openConnection();
        try {
            output = new ObjectOutputStream(socket.getOutputStream());
            input = new ObjectInputStream(socket.getInputStream());

            run();

            output.close();
            input.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void run() {
        receiveDimensions();
        while (true) {
            setChunk();
            invokePool();
            endPool();
            createNewPool();
            sendChunk();
        }
    }

    private void receiveDimensions() {
        try {
            ClientMaster.setMasterHeight(input.read());
            ClientMaster.setMasterWidth(input.read());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setChunk() {
        try {
            AlloyAtom[][] chunk = (AlloyAtom[][]) input.readObject();
            ClientMaster.setChunk(chunk);
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Disonnected.");
            System.exit(-1);
        }
    }

    private void sendChunk() {
        try {
            output.writeObject(ClientMaster.getChunk());
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createNewPool(){
        f = new ForkJoinPool();
    }

    private void invokePool() {
        WorkerThread w = new WorkerThread();
        f.invoke(w);
    }

    private void endPool(){
        f.shutdownNow();
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
