package Server;

import java.io.Serializable;

public class Area implements Serializable {
    private int startHeight;
    private int endHeight;
    private int startWidth;
    private int endWidth;

    public Area(int startHeight, int endHeight, int startWidth, int endWidth) {
        this.startHeight = startHeight;
        this.endHeight = endHeight;
        this.startWidth = startWidth;
        this.endWidth = endWidth;
    }

    public int getStartHeight() {
        return startHeight;
    }

    public int getEndHeight() {
        return endHeight;
    }

    public int getStartWidth() {
        return startWidth;
    }

    public int getEndWidth() {
        return endWidth;
    }

    @Override
    public String toString() {
        return "Area{" +
                "startHeight=" + startHeight +
                ", endHeight=" + endHeight +
                ", startWidth=" + startWidth +
                ", endWidth=" + endWidth +
                '}';
    }
}
