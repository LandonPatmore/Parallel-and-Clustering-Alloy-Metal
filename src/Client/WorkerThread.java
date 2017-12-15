package Client;

import java.util.concurrent.RecursiveAction;

public class WorkerThread extends RecursiveAction {
    private final double[] CONSTANTS = new double[]{0.75, 1.0, 1.25};
    private int START_HEIGHT;
    private int END_HEIGHT;
    private int START_WIDTH;
    private int END_WIDTH;
    private double[][] temps = ClientMaster.getTemps();
    private AlloyAtom[][] chunk = ClientMaster.getChunk();
    private double[][] tempArray;

    WorkerThread() {
        this.START_HEIGHT = 0;
        this.END_HEIGHT = chunk.length;
        this.START_WIDTH = 0;
        this.END_WIDTH = chunk[0].length;
    }

    private WorkerThread(int startHeight, int endHeight, int startWidth, int endWidth) {
        this.START_HEIGHT = startHeight;
        this.END_HEIGHT = endHeight;
        this.START_WIDTH = startWidth;
        this.END_WIDTH = endWidth;
    }

    @Override
    protected void compute() {
        generateTempArray();

        int height = END_HEIGHT - START_HEIGHT;
        int width = END_WIDTH - START_WIDTH;
        int workLoad = height * width;

        System.out.println("Workload: " + workLoad);

        if (workLoad > 40000) {
            System.out.println("Splitting workload: " + workLoad);
            splitMatrix();
        } else {
            heatUp();
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

    private void generateTempArray() {
        tempArray = new double[END_HEIGHT - START_HEIGHT][END_WIDTH - START_WIDTH];
    }

    private void heatUp() {
        for (int i = 0; i < (END_HEIGHT - START_HEIGHT); i++) {
            for (int j = 0; j < (END_WIDTH - START_WIDTH); j++) {
                AlloyAtom atom = chunk[i][j];
                if (atom.getX() == 0 && atom.getY() == 0) {
                    tempArray[i][j] = 500;
                } else if (atom.getX() == END_HEIGHT - 1 && atom.getY() == END_WIDTH - 1) {
                    tempArray[i][j] = 500;
                } else {
                    double temp = algorithm(atom);
                    tempArray[i][j] = temp;
                }
            }
        }
        transferFrom(tempArray);
    }

    private void transferFrom(double[][] array) {
        for (int i = 0; i < (END_HEIGHT - START_HEIGHT); i++) {
            for (int j = 0; j < (END_WIDTH - START_WIDTH); j++) {
                ClientMaster.getTemps()[i][j] = array[i][j];
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
                double temp = temps[q.getX()][q.getY()];
                double p = q.getMetals()[i];

                inner += temp * p;
            }
            total += CONSTANTS[i] * inner;
        }

        return total / a.getNeighbors().size();
    }
}
