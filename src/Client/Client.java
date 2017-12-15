package Client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client {
    private Socket socket;

    private DataOutputStream output;
    private DataInputStream input;
    private int chunkHeight;
    private int chunkWidth;
    private double[][] chunk;

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

    private void run() {
        generateRegion();
        receiveMasterDimensions();
        receiveArray();
    }

    private void receiveArray() {
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

        chunk = temp;
    }

    private void generateRegion() {
        AlloyAtom[][] region = new AlloyAtom[chunkHeight][chunkWidth];
        for (int i = 0; i < region.length; i++) {
            for (int j = 0; j < region[i].length; i++) {
                region[i][j] = new AlloyAtom(i, j);
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
