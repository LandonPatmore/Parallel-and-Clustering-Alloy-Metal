package CSC375HW3;


import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class AlloyAtom {
    private volatile double[] metals;
    private volatile double currentTemp;
    private int x, y;
    private ArrayList<AlloyAtom> neighbors;

    AlloyAtom(int x, int y) {
        this.metals = setMetalPercentages();
        this.currentTemp = 0.0;
        this.x = x;
        this.y = y;
    }

    AlloyAtom(int x, int y, double[] metals) {
        this.metals = metals;
        this.currentTemp = 0.0;
        this.x = x;
        this.y = y;
    }

    double getCurrentTemp() {
        return currentTemp;
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

    double[] getMetals() {
        return metals;
    }

    int getX() {
        return x;
    }

    int getY() {
        return y;
    }

    void setTemp(double temp) {
        currentTemp = temp;
    }

    Color calc() {
        double red;
        double green;
        double blue;

        if (currentTemp > 10) {
            red = 255;
            green = currentTemp;
            green = 9.4708025861 * Math.log(green) - 16.1195681661;

            if (currentTemp >= 19) {
                blue = 0;
            } else {
                blue = currentTemp - 1;
                blue = 13.5177312231 * Math.log(blue) - 30.0447927307;
            }
        } else {
            red = currentTemp - 6;
            red = 60.698727446 * Math.pow(red, -0.1332047592);

            green = currentTemp - 6;
            green = 60.1221695283 * Math.pow(green, -0.0755148492);

            blue = 255;
        }

        if (red < 0 || Double.isNaN(red)) {
            red = 0;
        } else if (red > 255) {
            red = 255;
        }
        if (green < 0 || Double.isNaN(green)) {
            green = 0;
        } else if (green > 255) {
            green = 255;
        }
        if (blue < 0 || Double.isNaN(blue)) {
            blue = 0;
        } else if (blue > 255) {
            blue = 255;
        }


        return new Color((float) (red / 255), (float) (green / 255), (float) (blue / 255));
    }
}