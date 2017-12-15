package Server;

import Global.AlloyAtom;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.Phaser;

public class ServerWorker implements Runnable {
    private Socket socket;
    private volatile AlloyAtom[][] blockA;
    private volatile AlloyAtom[][] blockB;
    private volatile AlloyAtom[][] chunkA;
    private volatile AlloyAtom[][] chunkB;

    private final int startHeight;
    private final int endHeight;
    private final int startWidth;
    private final int endWidth;

    private ObjectInputStream input;
    private ObjectOutputStream output;

    private Phaser phaser;

    public ServerWorker(Socket socket, Area area, Phaser phaser) {
        this.socket = socket;
        this.blockA = ServerMaster.getBlockA();
        this.blockB = ServerMaster.getBlockB();
        this.startHeight = area.getStartHeight();
        this.endHeight = area.getEndHeight();
        this.startWidth = area.getStartWidth();
        this.endWidth = area.getEndWidth();
        this.chunkA = new AlloyAtom[endHeight - startHeight][endWidth - startWidth];
        this.chunkB = new AlloyAtom[endHeight - startHeight][endWidth - startWidth];
        this.phaser = phaser;
    }

    @Override
    public void run() {
        System.out.println(socket.getInetAddress().getHostName() + " has connected.");
        try {
            setStreams();
            generateChunks();
            writeDimensions();

            communicateWithClient();

            output.close();
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void communicateWithClient() {
        while (true) {
            writeChunk();
            try {
                AlloyAtom[][] toBeAdded = (AlloyAtom[][]) input.readObject();
                System.out.println("Printing Chunk Received to client...Iteration: " + phaser.getPhase());
                for (int i = 0; i < toBeAdded.length; i++) {
                    for (int j = 0; j < toBeAdded[0].length; j++) {
                        System.out.print(toBeAdded[i][j].getCurrentTemp() + " ");
                    }
                    System.out.println();
                }
                transferToMainBlocks(toBeAdded);
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Client Disconnected.");
                System.exit(-1);
            }
            phaser.arriveAndAwaitAdvance();
        }
    }

    private void writeChunk() {
        try {
            AlloyAtom[][] selected;
            if (phaser.getPhase() % 2 == 0) {
                selected = chunkA;
            } else {
                selected = chunkB;
            }
            System.out.println("Printing Chunk Sent to client...Iteration: " + phaser.getPhase());
            for (int i = 0; i < chunkA.length; i++) {
                for (int j = 0; j < chunkA[0].length; j++) {
                    System.out.print(selected[i][j].getCurrentTemp() + " ");
                }
                System.out.println();
            }
            output.writeObject(selected);
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setStreams() {
        try {
            input = new ObjectInputStream(socket.getInputStream());
            output = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeDimensions() {
        try {
            output.write(blockA.length);
            output.write(blockB[0].length);
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void generateChunks() {
        int x = 0;
        for (int i = startHeight; i < endHeight; i++) {
            int y = 0;
            for (int j = startWidth; j < endWidth; j++) {
                AlloyAtom a = blockA[i][j];
                AlloyAtom b = blockB[i][j];

                chunkA[x][y] = a;
                chunkB[x][y] = b;
                y++;
            }
            x++;
        }
    }

    private void transferToMainBlocks(AlloyAtom[][] chunk) {
        AlloyAtom[][] transferTo;

        if (phaser.getPhase() % 2 == 0) {
            transferTo = blockB;
        } else {
            transferTo = blockA;
        }

        int x = 0;
        for (int i = startHeight; i < endHeight; i++) {
            int y = 0;
            for (int j = startWidth; j < endWidth; j++) {
                System.out.println("Added element.");
                transferTo[i][j].setTemp(chunk[x][y].getCurrentTemp());
                y++;
            }
            x++;
        }

//        System.out.println("Chunk transferred to block...");
    }
}
