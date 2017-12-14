package Client;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ForkJoinPool;

public class Runner {
    private ForkJoinPool f;
    private AlloyAtom[][] blockA;
    private AlloyAtom[][] blockB;


    public Runner(int iterations, int height, double heatTop, double heatBottom) {
        blockA = new AlloyAtom[height][height * 2];
        blockB = new AlloyAtom[height][height * 2];
        f = new ForkJoinPool();
        init();
        Master.setParams(iterations, heatTop, heatBottom);
    }

    void init() {
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

    void run() {
        System.out.println("Invoking Pool...");
        f.invoke(new WorkerThread());
    }

    void stopPool() {
        f.shutdownNow();
    }

    public void buildImage() {
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
