package Client;

import Global.AlloyAtom;

public class ClientMaster {
    private static AlloyAtom[][] chunk;
    private static int masterHeight;
    private static int masterWidth;

    private ClientMaster() {
    }

    public static void setChunk(AlloyAtom[][] chunk) {
        ClientMaster.chunk = chunk;
    }

    public static void setMasterHeight(int masterHeight) {
        ClientMaster.masterHeight = masterHeight;
    }

    public static void setMasterWidth(int masterWidth) {
        ClientMaster.masterWidth = masterWidth;
    }

    public static AlloyAtom[][] getChunk() {
        return chunk;
    }

    public static int getMasterHeight() {
        return masterHeight;
    }

    public static int getMasterWidth() {
        return masterWidth;
    }
}