package Server;

import Global.AlloyAtom;

import java.util.concurrent.Phaser;

public class Master {
    private static AlloyAtom[][] blockA;
    private static AlloyAtom[][] blockB;
    private static Phaser phaser = new Phaser();
    private static double heatTop;
    private static double heatBottom;
    private static int iterations;

    private Master() {
    }

    public static void setBlocks(AlloyAtom[][] a, AlloyAtom[][] b) {
        Master.blockA = a;
        Master.blockB = b;
    }

    public static void setParams(int iterations, double heatTop, double heatBottom) {
        Master.iterations = iterations;
        Master.heatTop = heatTop;
        Master.heatBottom = heatBottom;
    }

    public static double getHeatTop() {
        return heatTop;
    }

    public static double getHeatBottom() {
        return heatBottom;
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
