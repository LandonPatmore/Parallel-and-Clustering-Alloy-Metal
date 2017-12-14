package Client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Client {
    private Socket socket;

    public Client(){
        openConnection();
        try {
            OutputStream output = socket.getOutputStream();
            InputStream input = socket.getInputStream();

            String message = new String(input.readAllBytes());

            System.out.println(message);

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
