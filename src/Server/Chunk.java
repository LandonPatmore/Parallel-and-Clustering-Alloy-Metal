package Server;

public class Chunk {
    private int startHeight;
    private int endHeight;
    private int startWidth;
    private int endWidth;

    public Chunk(int startHeight, int endHeight, int startWidth, int endWidth) {
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
        return "Chunk{" +
                "startHeight=" + startHeight +
                ", endHeight=" + endHeight +
                ", startWidth=" + startWidth +
                ", endWidth=" + endWidth +
                '}';
    }
}
