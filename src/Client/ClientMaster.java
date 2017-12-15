package Client;

import Global.AlloyAtom;
import Server.ServerMaster;

import java.util.concurrent.Phaser;

public class ClientMaster {
    private static AlloyAtom[][] chunk;

    private ClientMaster() {
    }

    public static void setChunk(AlloyAtom[][] chunk) {
        ClientMaster.chunk = chunk;
    }

    public static AlloyAtom[][] getChunk() {
        return chunk;
    }
}
