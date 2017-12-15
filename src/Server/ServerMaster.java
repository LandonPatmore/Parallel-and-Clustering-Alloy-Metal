package Server;

import java.util.concurrent.Phaser;

public class ServerMaster {
    private volatile static double[][] blockA;
    private volatile static double[][] blockB;
    private static Phaser phaser = new Phaser();

    private ServerMaster() {
    }

    public static void setBlocks(double[][] a, double[][] b) {
        ServerMaster.blockA = a;
        ServerMaster.blockB = b;
    }

    public static Phaser getPhaser() {
        return phaser;
    }

    public static double[][] getBlockA() {
        return blockA;
    }

    public static double[][] getBlockB() {
        return blockB;
    }

}
