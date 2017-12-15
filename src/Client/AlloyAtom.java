package Client;


import java.util.ArrayList;
import java.util.Random;

public class AlloyAtom {
    private volatile double[] metals;
    private int x, y;
    private ArrayList<AlloyAtom> neighbors;

    public AlloyAtom(int x, int y) {
        this.metals = setMetalPercentages();
        this.x = x;
        this.y = y;
    }

    public AlloyAtom() {
    }

    private double[] setMetalPercentages() {
        Random r = new Random();
        double m1 = r.nextDouble();
        double m2 = r.nextDouble() * (1.0 - m1);
        double m3 = 1.0 - m1 - m2;

        return new double[]{m1, m2, m3};
    }

    public void setNeighbors(AlloyAtom[][] a) {
        ArrayList<AlloyAtom> neighbors = new ArrayList<>();
        int rowIndexes[] = {-1, 0, 0, 1};
        int colIndexes[] = {0, -1, 1, 0};

        for (int i = 0; i < rowIndexes.length; i++) {
            int x = this.x + rowIndexes[i];
            int y = this.y + colIndexes[i];

            if (x >= 0 && y >= 0 && x < a.length && y < a[0].length) {
                neighbors.add(a[x][y]);
            }
        }

        this.neighbors = neighbors;
    }

    public ArrayList<AlloyAtom> getNeighbors() {
        return neighbors;
    }

    public double[] getMetals() {
        return metals;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
