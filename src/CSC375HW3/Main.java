package CSC375HW3;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        Runner r = new Runner(500, 200, 500000000000000.0, 500000000000000.0);
        r.run();

        r.stopPool();

        r.buildImage();

        System.out.println("Done.");


    }

}
