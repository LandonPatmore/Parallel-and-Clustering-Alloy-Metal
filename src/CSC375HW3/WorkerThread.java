package CSC375HW3;

import java.util.concurrent.Phaser;
import java.util.concurrent.RecursiveAction;

public class WorkerThread extends RecursiveAction {
    private volatile AlloyAtom[][] blockA = Master.getBlockA();
    private volatile AlloyAtom[][] blockB = Master.getBloockB();
    private final double[] CONSTANTS = new double[]{0.75, 1.0, 1.25};
    private final Phaser phaser = Master.getPhaser();
    private final int iterations = Master.getIterations();
    private int START_HEIGHT;
    private int END_HEIGHT;
    private int START_WIDTH;
    private int END_WIDTH;
    private final double HEAT_TOP = Master.getHeatTop();
    private final double HEAT_BOTTOM = Master.getHeatBottom();

    WorkerThread() {
        this.START_HEIGHT = 0;
        this.END_HEIGHT = blockA.length;
        this.START_WIDTH = 0;
        this.END_WIDTH = blockA[0].length;
    }

    private WorkerThread(int startHeight, int endHeight, int startWidth, int endWidth) {
        this.START_HEIGHT = startHeight;
        this.END_HEIGHT = endHeight;
        this.START_WIDTH = startWidth;
        this.END_WIDTH = endWidth;
    }

    @Override
    protected void compute() {
        int height = END_HEIGHT - START_HEIGHT;
        int width = END_WIDTH - START_WIDTH;
        int workLoad = height * width;

        System.out.println(workLoad);

        if (workLoad > 40000) {
            System.out.println("Splitting workload: " + workLoad);
            splitMatrix();
        } else {
            for (int i = 0; i < iterations; i++) {
                phaser.register();
                heatUp(i);
                phaser.arriveAndAwaitAdvance();
                phaser.arriveAndDeregister();
            }
        }
    }

    private void splitMatrix() {
        int midHeight = (int) Math.floor((END_HEIGHT - START_HEIGHT) / 2 + START_HEIGHT);
        int midWidth = (int) Math.floor((END_WIDTH - START_WIDTH) / 2 + START_WIDTH);

        WorkerThread topLeftWorker = new WorkerThread(START_HEIGHT, midHeight, START_WIDTH, midWidth);
        WorkerThread topRightWorker = new WorkerThread(START_HEIGHT, midHeight, midWidth, END_WIDTH);
        WorkerThread bottomLeftWorker = new WorkerThread(midHeight, END_HEIGHT, START_WIDTH, midWidth);
        WorkerThread bottomRightWorker = new WorkerThread(midHeight, END_HEIGHT, midWidth, END_WIDTH);

        invokeAll(topLeftWorker, topRightWorker, bottomLeftWorker, bottomRightWorker);
    }

    private void heatUp(int iteration) {
        AlloyAtom[][] workingOn;
        AlloyAtom[][] transferTo;
        if (iteration % 2 == 0) {
            workingOn = blockA;
            transferTo = blockB;
        } else {
            workingOn = blockB;
            transferTo = blockA;
        }

        for (int i = START_HEIGHT; i <= END_HEIGHT - 1; i++) {
            for (int j = START_WIDTH; j <= END_WIDTH - 1; j++) {
                AlloyAtom atom = workingOn[i][j];
                if (i == 0 && j == 0) {
                    transferTo[i][j].setTemp(HEAT_TOP);
                } else if (i == workingOn.length - 1 && j == workingOn[i].length - 1) {
                    transferTo[i][j].setTemp(HEAT_BOTTOM);
                } else {
                    double temp = algorithm(atom);
                    transferTo[i][j].setTemp(temp);
                }
            }
        }
    }

    /**
     * Algorithm for determining heat of each atom:
     * m = each of the three base metals
     * Cm = thermal constant for the currently chosen metal m
     * N = the set of neighboring regions
     * tempn = temperature of neighboring region
     * pmn = the percentage of metal m in neighbor n
     * |N| = the number of neighboring regions
     */

    private double algorithm(AlloyAtom a) {
        double total = 0.0;

        for (int i = 0; i < CONSTANTS.length; i++) {
            double inner = 0.0;
            for (AlloyAtom q : a.getNeighbors()) {
                double temp = q.getCurrentTemp();
                double p = q.getMetals()[i];

                inner += temp * p;
            }
            total += CONSTANTS[i] * inner;
        }

        return total / a.getNeighbors().size();
    }
}
