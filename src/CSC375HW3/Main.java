package CSC375HW3;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        if (args.length > 0) {
            int iterations = Integer.parseInt(args[0]);
            int height = Integer.parseInt(args[1]);
            double heatTop = Double.parseDouble(args[2]);
            double heatBottom = Double.parseDouble(args[3]);

            Runner r = new Runner(iterations, height, heatTop, heatBottom);
            r.run();

            r.stopPool();

            r.buildImage();

            System.out.println("Done.");

        }
    }

}
