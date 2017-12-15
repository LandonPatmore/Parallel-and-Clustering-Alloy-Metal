package Server;

import Global.AlloyAtom;

import java.util.concurrent.Phaser;

public class ServerMaster {
    private volatile static AlloyAtom[][] blockA;
    private volatile static AlloyAtom[][] blockB;
    private static Phaser phaser = new Phaser();
    private static int iterations;

    private ServerMaster() {
    }

    public static void setBlocks(AlloyAtom[][] a, AlloyAtom[][] b) {
        ServerMaster.blockA = a;
        ServerMaster.blockB = b;
    }

    public static void setParams(int iterations) {
        ServerMaster.iterations = iterations;
    }

    public static int getIterations() {
        return iterations;
    }

    public static Phaser getPhaser() {
        return phaser;
    }

    public static AlloyAtom[][] getBlockA() {
        return blockA;
    }

    public static AlloyAtom[][] getBlockB() {
        return blockB;
    }

}
