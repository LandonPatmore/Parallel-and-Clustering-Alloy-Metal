package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.Phaser;

public class ServerWorker implements Runnable {
    private Socket socket;
    private volatile double[][] blockA;
    private volatile double[][] blockB;

    private final int startHeight;
    private final int endHeight;
    private final int startWidth;
    private final int endWidth;

    private DataInputStream input;
    private DataOutputStream output;

    private Phaser phaser;

    public ServerWorker(Socket socket, Area area, Phaser phaser) {
        this.socket = socket;
        this.blockA = ServerMaster.getBlockA();
        this.blockB = ServerMaster.getBlockB();
        this.startHeight = area.getStartHeight();
        this.endHeight = area.getEndHeight();
        this.startWidth = area.getStartWidth();
        this.endWidth = area.getEndWidth();
        this.phaser = phaser;
    }

    @Override
    public void run() {
        System.out.println(socket.getInetAddress().getHostName() + " has connected.");
        try {
            setStreams();
            writeMasterDimensions();

            communicateWithClient();

            output.close();
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void communicateWithClient() {
        for (int i = 0; i < 1000; i++) {
            phaser.arriveAndAwaitAdvance();
            writeArray();
            System.out.println("\nPrinting Chunk Received from client...Iteration: " + phaser.getPhase());
            readArray();
        }
    }

    private void readArray() {
        double[][] transfer = new double[endHeight - startHeight][endWidth - startWidth];
        for (int i = 0; i < transfer.length; i++) {
            for (int j = 0; j < transfer[i].length; j++) {
                try {
                    transfer[i][j] = input.readDouble();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        transferToMainBlocks(transfer);
    }

    private void writeArray() {
        try {
            double[][] selected;
            if (phaser.getPhase() % 2 == 0) {
                selected = blockA;
            } else {
                selected = blockB;
            }

            System.out.println("\nPrinting Chunk Sent to client...Iteration: " + phaser.getPhase());
            for (int i = startHeight; i < endHeight; i++) {
                for (int j = startWidth; j < endWidth; j++) {
                    output.writeDouble(selected[i][j]);
                    System.out.print(selected[i][j] + " ");
                }
                System.out.println();
            }

            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setStreams() {
        try {
            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeMasterDimensions() {
        try {
            output.write(blockA.length);
            output.write(blockA[0].length);
            output.write(endHeight - startHeight);
            output.write(endWidth - startWidth);
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void transferToMainBlocks(double[][] chunk) {
        double[][] transferTo;

        if (phaser.getPhase() % 2 == 0) {
            transferTo = blockB;
        } else {
            transferTo = blockA;
        }

        int x = 0;
        for (int i = startHeight; i < endHeight; i++) {
            int y = 0;
            for (int j = startWidth; j < endWidth; j++) {
                transferTo[i][j] = chunk[x][y];
                y++;
            }
            x++;
        }

        System.out.println();
    }
}
