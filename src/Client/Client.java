package Client;

import Global.AlloyAtom;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;

public class Client {
    private Socket socket;

    public Client(){
        openConnection();
        try {
            OutputStream output = socket.getOutputStream();
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());

            try {
                AlloyAtom[][] chunk = (AlloyAtom[][])input.readObject();
                System.out.println(Arrays.deepToString(chunk));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            output.close();
            input.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openConnection(){
        try {
            socket = new Socket("nuc29.local", 9000);
        } catch (IOException e) {
            System.out.println("Cannot connect to host.");
            System.exit(-1);
        }
    }

}
