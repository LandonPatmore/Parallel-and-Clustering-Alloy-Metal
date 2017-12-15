package Server;

import Global.AlloyAtom;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientWorker implements Runnable {
    private Socket socket;
    private AlloyAtom[][] blockA;
    private AlloyAtom[][] blockB;
    private AlloyAtom[][] chunkA;
    private AlloyAtom[][] chunkB;

    private Area area;

    private int iteration;
    private final int startHeight;
    private final int endHeight;
    private final int startWidth;
    private final int endWidth;

    public ClientWorker(Socket socket, AlloyAtom[][] blockA, AlloyAtom[][] blockB, Area area) {
        this.socket = socket;
        this.blockA = blockA;
        this.blockB = blockB;
        this.area = area;
        this.iteration = 0;
        this.startHeight = area.getStartHeight();
        this.endHeight = area.getEndHeight();
        this.startWidth = area.getStartWidth();
        this.endWidth = area.getEndWidth();
        this.chunkA = new AlloyAtom[endHeight - startHeight][endWidth - startWidth];
        this.chunkB = new AlloyAtom[endHeight - startHeight][endWidth - startWidth];
    }

    @Override
    public void run() {
        try {
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            generateChunks();
            output.writeObject(chunkA);

            chunkA = (AlloyAtom[][]) input.readObject();

            transferToMainBlocks();

            output.close();
            input.close();
            System.out.println(socket.getInetAddress().getHostName() + " has connected.");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void generateChunks() {
        int x = 0;
        for (int i = startHeight; i < endHeight; i++) {
            int y = 0;
            for (int j = startWidth; j < endWidth; j++) {
                AlloyAtom a = blockA[i][j];
                AlloyAtom b = blockB[i][j];

                //chunkA[x][y] = new AlloyAtom(a.getX(), a.getY(), a.getMetals());
                //chunkB[x][y] = new AlloyAtom(b.getX(), b.getY(), b.getMetals());
                chunkA[x][y] = a;
                chunkB[x][y] = b;
                y++;
            }
            x++;
        }
    }

    private void transferToMainBlocks() {
        AlloyAtom[][] workingOnChunk;
        AlloyAtom[][] transferTo;

        if (iteration % 2 == 0) {
            workingOnChunk = chunkA;
            transferTo = blockB;
        } else {
            workingOnChunk = chunkB;
            transferTo = blockB;
        }
		
		int x = 0;
        for (int i = startHeight; i < endHeight; i++) {
        	int y = 0;
            for (int j = startWidth; j < endWidth; j++) {
                transferTo[i][j].setTemp(workingOnChunk[x][y].getCurrentTemp());
            	y++;
            }
            x++;
        }

        for(int i = 0; i < blockB.length; i++){
        	for(int j = 0; j < blockB[i].length; j++){
        		System.out.print(blockB[i][j] + " ");
        	}
        	System.out.println();
        }
    }

    private AlloyAtom[][] switchChunk() {
        // just finds the block to work on
        if (iteration % 2 == 0) {
            return chunkA;
        }

        return chunkB;
    }


}
