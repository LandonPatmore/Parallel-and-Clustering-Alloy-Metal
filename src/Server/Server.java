package Server;

import Global.AlloyAtom;
import Global.Area;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private ServerSocket server;
    private boolean isStopped = false;
    private int port;
    private int height;
    private AlloyAtom[][] blockA;
    private AlloyAtom[][] blockB;
    private Area[] areas;

    public Server(int port) {
        this.port = port;
        openServerSocket(port);
    }

    public void startServer() {
        System.out.println("Creating alloy blocks...");
        height = 4;
        initParams(100, height, 100, 100);
        System.out.println("Blocks created...");
        System.out.println("Splitting into chunks...");
        splitIntoChunks();
        System.out.println("Chunks split...");
        int i = 0;
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
            new Thread(new ClientWorker(clientSocket, blockA, blockB, areas[i++])).start();
        }
    }

    private void openServerSocket(int port) {
        try {
            server = new ServerSocket(port);
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port " + port, e);
        }
    }

    private void initParams(int iterations, int height, double heatTop, double heatBottom) {
        blockA = new AlloyAtom[height][height * 2];
        blockB = new AlloyAtom[height][height * 2];

        Master.setParams(iterations, heatTop, heatBottom);

        for (int i = 0; i < blockA.length; i++) {
            for (int j = 0; j < blockA[i].length; j++) {
                AlloyAtom a = new AlloyAtom(i, j);
                AlloyAtom b = new AlloyAtom(i, j, a.getMetals());
                blockA[i][j] = a;
                blockB[i][j] = b;
            }
        }

        for (int i = 0; i < blockA.length; i++) {
            for (int j = 0; j < blockA[i].length; j++) {
                blockA[i][j].setNeighbors(blockA);
                blockB[i][j].setNeighbors(blockB);
            }
        }

        Master.setBlocks(blockA, blockB);
    }

    // Works on 4 computers this server, will figure out scaling later
    private void splitIntoChunks() {
        areas = new Area[4];
        int width = height * 2;

        int midHeight = (int) Math.floor((height) / 2);
        int midWidth = (int) Math.floor((width) / 2);

        areas[0] = new Area(0, midHeight, 0, midWidth);
        areas[1] = new Area(0, midHeight, midWidth, width);
        areas[2] = new Area(midHeight, height, 0, midWidth);
        areas[3] = new Area(midHeight, height, midWidth, width);


    }
}
