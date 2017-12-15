package Client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ForkJoinPool;

public class Client {
    private Socket socket;

    private DataOutputStream output;
    private DataInputStream input;
    private int chunkHeight;
    private int chunkWidth;
    private static ForkJoinPool forkJoinPool = new ForkJoinPool();;

    public Client() {
        openConnection();
        try {
            output = new DataOutputStream(socket.getOutputStream());
            input = new DataInputStream(socket.getInputStream());

            run();

            output.close();
            input.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void run() throws IOException {
        receiveMasterDimensions();
        generateRegion();

        for (int i = 0; i < 1000; i++) {
            receiveArray();
            workOnArray();
            sendArray();
        }
        input.close();
        output.close();
    }

    private void sendArray() {
        System.out.println("Sending array to server...");
        double[][] temp = ClientMaster.getTemps();
        for (int i = 0; i < chunkHeight; i++) {
            for (int j = 0; j < chunkWidth; j++) {
                try {
                    output.writeDouble(temp[i][j]);
                    System.out.print(temp[i][j] + " ");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            System.out.println();
        }
    }

    private void receiveArray() {
        System.out.println("Receiving array from server...");
        double[][] temp = new double[chunkHeight][chunkWidth];
        for (int i = 0; i < chunkHeight; i++) {
            for (int j = 0; j < chunkWidth; j++) {
                try {
                    temp[i][j] = input.readDouble();
                    System.out.print(temp[i][j] + " ");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            System.out.println();
        }

        ClientMaster.setTemps(temp);
    }

    private void workOnArray() {
        System.out.println("Working on array.");
        forkJoinPool.invoke(new WorkerThread());
        System.out.println("Done");
    }

    private void generateRegion() {
        AlloyAtom[][] region = new AlloyAtom[chunkHeight][chunkWidth];
        for (int i = 0; i < region.length; i++) {
            for (int j = 0; j < region[i].length; j++) {
                region[i][j] = new AlloyAtom(i, j);
            }
        }

        for (int i = 0; i < region.length; i++) {
            for (int j = 0; j < region[i].length; j++) {
                region[i][j].setNeighbors(region);
            }
        }

        ClientMaster.setChunk(region);
    }

    private void receiveMasterDimensions() {
        try {
            ClientMaster.setMasterHeight(input.read());
            ClientMaster.setMasterWidth(input.read());
            chunkHeight = input.read();
            chunkWidth = input.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
