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
    private volatile double[][] chunkA;
    private volatile double[][] chunkB;

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
        this.chunkA = new double[endHeight - startHeight][endWidth - startWidth];
        this.chunkB = new double[endHeight - startHeight][endWidth - startWidth];
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
        writeArray();
        double[][] readArray = new double[endHeight - startHeight][endWidth - startWidth];
//        System.out.println("\nPrinting Chunk Received from client...Iteration: " + phaser.getPhase());
//        readArray(readArray);
//        phaser.arriveAndAwaitAdvance();
    }

    private void readArray(double[][] readArray) {
        for (int i = 0; i < readArray.length; i++) {
            for (int j = 0; j < readArray[i].length; j++) {
                try {
                    readArray[i][j] = input.readDouble();
                    System.out.print(readArray[i][j] + " ");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            System.out.println();
        }

    }

    private void writeArray() {
        try {
            double[][] selected;
            if (phaser.getPhase() % 2 == 0) {
                selected = chunkA;
            } else {
                selected = chunkB;
            }

            System.out.println("\nPrinting Chunk Sent to client...Iteration: " + phaser.getPhase());
            for (int i = 0; i < selected.length; i++) {
                for (int j = 0; j < selected[0].length; j++) {
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
            output.write(blockB[0].length);
            output.write(chunkA.length);
            output.write(chunkA[0].length);
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    private void transferToMainBlocks(AlloyAtom[][] chunk) {
//        AlloyAtom[][] transferTo;
//
//        if (phaser.getPhase() % 2 == 0) {
//            transferTo = blockB;
//        } else {
//            transferTo = blockA;
//        }
//
//        int x = 0;
//        for (int i = startHeight; i < endHeight; i++) {
//            int y = 0;
//            for (int j = startWidth; j < endWidth; j++) {
//                System.out.println("Added element.");
//                transferTo[i][j].setTemp(chunk[x][y].getCurrentTemp());
//                y++;
//            }
//            x++;
//        }
//    }
}
