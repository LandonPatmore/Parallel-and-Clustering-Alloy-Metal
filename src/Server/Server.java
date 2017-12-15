package Server;

import Global.AlloyAtom;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Phaser;

public class Server {

    private ServerSocket server;
    private int port;
    private final int height;
    private AlloyAtom[][] blockA;
    private AlloyAtom[][] blockB;
    private Area[] areas;
    private Phaser phaser;

    public Server(int port) {
        this.port = port;
        openServerSocket(port);
        height = 4;
        phaser = new Phaser();
        phaser.bulkRegister(4);
    }

    public void startServer() {
        System.out.println("Server started on port: " + port + "\n");

        System.out.println("Creating alloy blocks...");
        initParams(100, height);
        System.out.println("Blocks created...");

        System.out.println("Splitting into chunks...");
        splitIntoChunks();
        System.out.println("Chunks split...");

        int i = 0;
        while (true) {
            Socket clientSocket;
            try {
                clientSocket = server.accept();
            } catch (IOException e) {
                throw new RuntimeException(
                        "Error accepting client connection", e);
            }
            new Thread(new ServerWorker(clientSocket, blockA, blockB, areas[i++], phaser)).start();
        }
    }

    private void openServerSocket(int port) {
        try {
            server = new ServerSocket(port);
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port " + port, e);
        }
    }

    private void initParams(int iterations, int height) {
        blockA = new AlloyAtom[height][height * 2];
        blockB = new AlloyAtom[height][height * 2];

        ServerMaster.setParams(iterations);

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

        ServerMaster.setBlocks(blockA, blockB);
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

    private void buildImage() {
        BufferedImage image = new BufferedImage(blockA.length * 2, blockA.length, BufferedImage.TYPE_4BYTE_ABGR);
        for (int i = 0; i < blockA.length; i++) {
            for (int j = 0; j < blockA[i].length; j++) {
                double temp = blockA[i][j].getCurrentTemp();
                if (temp > 255) {
                    temp = 255;
                }
                Color c = new Color((float) temp / 255, 0, 0);
                image.setRGB(j, i, c.getRGB());
            }
        }

        File outputfile = new File("image.png");
        try {
            ImageIO.write(image, "png", outputfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
