package Server;

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
    private volatile double[][] blockA;
    private volatile double[][] blockB;
    private Area[] areas;
    private Phaser phaser;

    public Server(int port) {
        this.port = port;
        openServerSocket(port);
        height = 200;
        phaser = ServerMaster.getPhaser();
        phaser.bulkRegister(4);
    }

    public void startServer() {
        System.out.println("Server started on port: " + port + "\n");

        initParams(height);

        System.out.println("Splitting into chunks...");
        splitIntoChunks();
        System.out.println("Chunks split...");

        int i = 0;
        while (true) {
            Socket clientSocket = null;
            try {
                clientSocket = server.accept();
            } catch (IOException | RuntimeException e) {
                buildImage();
            }
            if (i < 4) {
                new Thread(new ServerWorker(clientSocket, areas[i++], phaser)).start();
            }
        }
    }

    private void openServerSocket(int port) {
        try {
            server = new ServerSocket(port);
            server.setSoTimeout(30000);
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port " + port, e);
        }
    }

    private void initParams(int height) {
        blockA = new double[height][height * 2];
        blockB = new double[height][height * 2];

        ServerMaster.setBlocks(blockA, blockB);
    }

    // Works on 4 computers this server, will figure out scaling later
    private void splitIntoChunks() {
        areas = new Area[4];
        int width = height * 2;

        int midHeight = (int) Math.floor((height) / 2);
        int midWidth = (int) Math.floor((width) / 2);

        areas[0] = new Area(0, midHeight + 1, 0, midWidth + 1);
        areas[1] = new Area(0, midHeight + 1, midWidth - 1, width);
        areas[2] = new Area(midHeight - 1, height, 0, midWidth + 1);
        areas[3] = new Area(midHeight - 1, height, midWidth - 1, width);
    }

    private void buildImage() {
        BufferedImage image = new BufferedImage(blockA.length * 2, blockA.length, BufferedImage.TYPE_4BYTE_ABGR);
        for (int i = 0; i < blockA.length; i++) {
            for (int j = 0; j < blockA[i].length; j++) {
                double temp = blockA[i][j];
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
        System.out.println("Image created.");
    }
}
